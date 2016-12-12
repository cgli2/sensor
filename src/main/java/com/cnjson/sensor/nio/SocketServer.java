package com.cnjson.sensor.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnjson.sensor.config.Global;
import com.cnjson.sensor.db.entity.DataRecord;
import com.cnjson.sensor.db.service.DataRecordService;
import com.cnjson.sensor.util.ByteUtils;
import com.cnjson.sensor.util.CRC16;
import com.cnjson.sensor.util.DataFormater;

public class SocketServer {

	private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

	/**
	 * 创建非阻塞服务器绑定2020端口
	 */
	public SocketServer() {
		try {
			ss = ServerSocketChannel.open();
			Integer port = Global.getServerPort();
			ss.bind(new InetSocketAddress(port));
			ss.configureBlocking(false);
			selector = Selector.open();
			ss.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			logger.error(e.getMessage());
			close();
		}
	}

	private Map<Integer, Integer> rejectedThreadCountMap = new ConcurrentHashMap<>();

	private RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				int hashcode = r.hashCode();
				Integer count = rejectedThreadCountMap.get(hashcode);
				if (count == null) {
					count = 0;
					rejectedThreadCountMap.put(hashcode, count);
				} else {
					count++;
					rejectedThreadCountMap.put(hashcode, count);
				}
				if (count < 1) {
					executor.execute(r);
				} else {
					if (r instanceof WriteClientSocketHandler) {
						WriteClientSocketHandler realThread = (WriteClientSocketHandler) r;
						logger.info("服务系统繁忙,客户端WriteClientSocketHandler[" + realThread.client + "]请求被拒绝处理！");

						SelectionKey selectionKey = realThread.client.keyFor(selector);
						if (selectionKey != null) {
							selectionKey.cancel();
						}
						if (realThread.client != null) {
							try {
								realThread.client.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						AtomicBoolean isWriting = isWritingMap.get(realThread.client);
						isWriting.set(false);
					} else if (r instanceof ReadClientSocketHandler) {
						ReadClientSocketHandler realThread = (ReadClientSocketHandler) r;
						logger.info("服务系统繁忙,客户端ReadClientSocketHandler[" + realThread.client + "]请求被拒绝处理！");
						SelectionKey selectionKey = realThread.client.keyFor(selector);
						if (selectionKey != null) {
							selectionKey.cancel();
						}
						if (realThread.client != null) {
							try {
								realThread.client.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						AtomicBoolean isReading = isReadingMap.get(realThread.client);
						isReading.set(false);
					} else {
						logger.info("服务系统繁忙,系统线程[" + r.getClass().getName() + "]被拒绝处理！");
					}
				}
			} catch (InterruptedException e) {
				logger.info("RejectedExecutionHandler处理发生异常：" + e.getMessage());
			}
		}
	};

	private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(30, 50, 300, TimeUnit.MILLISECONDS,
			new ArrayBlockingQueue<Runnable>(5), rejectedExecutionHandler);
	private Map<SocketChannel, AtomicBoolean> isReadingMap = new ConcurrentHashMap<SocketChannel, AtomicBoolean>();
	private Map<SocketChannel, AtomicBoolean> isWritingMap = new ConcurrentHashMap<SocketChannel, AtomicBoolean>();
	private Selector selector = null;
	private ServerSocketChannel ss = null;
	private volatile boolean isClose = false;

	public boolean isClose() {
		return isClose;
	}

	/**
	 * 关闭服务器
	 */
	private void close() {
		isClose = true;
		threadPool.shutdown();
		try {
			if (ss != null) {
				ss.close();
			}
			if (selector != null) {
				selector.close();
			}
		} catch (IOException e) {
			logger.error("服务器关闭发生异常：" + e.getMessage());
		}
	}

	/**
	 * 启动选择器监听客户端事件
	 */
	private void start() {
		threadPool.execute(new SelectorGuardHandler());
	}

	/**
	 * Nio 通道选择器
	 * 
	 * @author cgli
	 *
	 */
	private class SelectorGuardHandler implements Runnable {

		@Override
		public void run() {

			while (!isClose) {
				try {
					if (selector.select(10) == 0) {
						continue;
					}
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey selectedKey = iterator.next();
						iterator.remove();
						try {
							if (selectedKey.isReadable()) {
								SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
								AtomicBoolean isReading = isReadingMap.get(socketChannel);
								if (isReading == null) {
									isReading = new AtomicBoolean(false);
									isReadingMap.put(socketChannel, isReading);
								}
								while (isReading.get()) {
									Thread.sleep(5);
								}
								isReading.set(true);
								threadPool.execute(new ReadClientSocketHandler(socketChannel));

							} else if (selectedKey.isWritable()) {
								Object responseMessage = selectedKey.attachment();
								SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
								selectedKey.interestOps(SelectionKey.OP_READ);
								logger.info("---------------------->isWritable");
								threadPool.execute(new WriteClientSocketHandler(socketChannel, responseMessage));

							} else if (selectedKey.isAcceptable()) {
								ServerSocketChannel ssc = (ServerSocketChannel) selectedKey.channel();
								SocketChannel clientSocket = ssc.accept();
								clientSocket.configureBlocking(false);
								logger.info("---------------------->isAcceptable");
								clientSocket.register(selector, SelectionKey.OP_READ);
							}
						} catch (CancelledKeyException e) {
							selectedKey.cancel();
							logger.error("服务器启动或运行发生异常：" + e);
						}
					}
				} catch (Exception e) {
					if (e instanceof NullPointerException) {
						e.printStackTrace();
						logger.error(e.getMessage());
						close();
					} else {
						logger.error("服务器启动或运行发生异常：" + e);
						close();
					}
					break;
				}
			}
		}
	}

	/**
	 * 
	 * 响应数据给客户端线程
	 * 
	 */
	private class WriteClientSocketHandler implements Runnable {
		private SocketChannel client;

		private Object responseMessage;

		private WriteClientSocketHandler(SocketChannel client, Object responseMessage) {
			this.client = client;
			this.responseMessage = responseMessage;
		}

		@Override
		public void run() {
			try {
				logger.info("------------------w1 准备写入数据-------------");
				byte[] responseByteData = null;
				String logResponseString = "";
				if (responseMessage instanceof byte[]) {
					responseByteData = (byte[]) responseMessage;
					logger.info("---------------w2 写字节数据-------------");
					logResponseString = DataFormater.bytesToHexString(responseByteData);
				} else if (responseMessage instanceof String) {
					logResponseString = (String) responseMessage;
					responseByteData = logResponseString.getBytes();
				} else if (responseMessage != null) {
					logger.info("不支持的数据类型" + responseMessage.getClass());
					return;
				}
				if (responseByteData == null || responseByteData.length == 0) {
					logger.info("服务器响应的写入数据为空");
					return;
				}
				client.write(ByteBuffer.wrap(responseByteData));

				// ByteBuffer data =
				// ByteBuffer.allocate(responseByteData.length);
				// data.putInt(responseByteData.length);
				// data.put(responseByteData);
				// data.flip();
				// while (data.hasRemaining()) { client.write(data); }

				logger.info("服务端响应客户端[" + client.getRemoteAddress() + "]写入数据 :[" + logResponseString + "]");
			} catch (Exception e) {
				try {
					logger.info("server响应客户端[" + client.getRemoteAddress() + "]数据 异常[" + e.getMessage() + "]");
					SelectionKey selectionKey = client.keyFor(selector);
					if (selectionKey != null) {
						selectionKey.cancel();
					}
					if (client != null) {
						client.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				AtomicBoolean isWriting = isWritingMap.get(client);
				isWriting.set(false);
			}
		}
	}

	/**
	 * 读客户端发送数据线程
	 * 
	 */
	private class ReadClientSocketHandler implements Runnable {

		private SocketChannel client;
		private ByteBuffer dataLen = ByteBuffer.allocate(4);

		private ReadClientSocketHandler(SocketChannel client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				dataLen.clear();
				client.read(dataLen);
				if (dataLen.array().length > 0 && dataLen.hasArray()) {
					String slen = DataFormater.byteToHex(dataLen.get(3));
					int dataLength = Integer.parseInt(slen, 16);
					if (dataLength <= 0) {
						return;
					}
					logger.info("datalen=" + dataLength);

					// CRC校验数据需要带上 长度位,即 长度到负载的CRC校验
					ByteBuffer crcCheckData = ByteBuffer.allocate(dataLength - 1);
					crcCheckData.put(dataLen.get(3));
					ByteBuffer data = ByteBuffer.allocate(dataLength + 2);
					while (data.hasRemaining()) {
						client.read(data);
					}
					// 数据长度值 = [帧代号1]+[功能码2]+[地址段m]+[数据n]+[CRC校验2]
					int subLeng = data.array().length;
					StringBuilder sb = new StringBuilder();
					sb.append(slen);
					for (int i = 0; i < subLeng; i++) {
						sb.append(" ");
						byte b = data.get(i);
						sb.append(DataFormater.byteToHex(b));
						if (i < subLeng - 4) {
							crcCheckData.put(b);
						}
					}
					logger.info(Thread.currentThread().getId() + " 接收数据 :[" + sb.toString() + "]");
					boolean isOK = checkCRCVal(crcCheckData, data);
					if (!isOK) {
						logger.warn("CRC验证不通过!");
						return;
					}
					byte[] response = null;
					// 帧代号1
					int flag = Integer.parseInt(DataFormater.byteToHex(data.get(0)), 16);
					// 设备登录
					if (flag == 0x41) {
						logger.info("---------0x41 设备登录注册-------------");
						response = buildRegisterPackage(data);

						// 设备心跳
					} else if (flag == 0x43) {

						// 心跳类型 1：链路心跳，2：状态心跳
						/*
						 * response = new byte[subLeng + 4];
						 * System.arraycopy(dataLen.array(), 0, response, 0, 4);
						 * System.arraycopy(data.array(), 0, response, 4,
						 * subLeng); response[2] = (byte) 0xAF; response[6] =
						 * 0x00; byte[] crcTemp = new byte[subLeng - 3];
						 * System.arraycopy(response, 3, crcTemp, 0,
						 * response.length - 7); byte[] nR =
						 * CRC16.getCrcBytes(crcTemp); System.arraycopy(nR, 0,
						 * response, response.length - 4, 2);
						 */

						int type = Integer.parseInt(DataFormater.byteToHex(data.get(8)), 16);
						byte[] content = null;// 应答内容，不同类型不同内容
						byte[] device = getDevice(data); // 设备地址段
						// 链路心跳
						if (type == 1) {
							content = new byte[] { 0x01, 0x00 };
						} else {
							content = new byte[23];
							content[0] = 0x02;
							content[1] = 0x00;

							content[2] = 0x00;// 如果要同步时间，必须修改1，同时把正确时间下发。
							content[3] = 0x00;
							System.arraycopy(data.array(), 10, content, 4, 19);
						}
						response = buildResponsePackage((byte) 0x43, device, content);

						logger.info("----------0x43 接收设备心跳并返回:" + DataFormater.bytesToHexString(response) + "----");
						// 客户端数据上报
					} else if (flag == 0x47) {
						byte[] device = getDevice(data); // 设备地址段
						String address = DataFormater.bytesToAddress(device);
						DataRecordService service = new DataRecordService();
						String equipmentId = service.getEquipmentIdByAddress(address);

						byte[] date = getData(data, 9, 6);// 日期时间段（年月日时分秒如：1608）
						Date sDate = DataFormater.bytesToDate(date);
						logger.info("-----------打印时间:" + sDate + "-----,设备地址---" + address);

						// SAVE TO DB.....

						// 通道数，一般固定为11个通道
						byte[] channelNum = getData(data, 10, 1);
						int num = ByteUtils.getInt(channelNum);
						// 通道数据
						List<DataRecord> records = new ArrayList<>();
						List<Object> checkItemIds = service.getCheckItemIds(equipmentId, num);
						for (int j = 0; j < num; j++) {
							//前三个设备状态信息
							byte[] d1 = getData(data, 11 + (j * 4), 4);
							Float f = DataFormater.bytesToFloat(d1);
							/**
							 * 电压mv; 电导率为ms; 溶解氧为mg/l ;温度为℃ ; 其他没有单位
							 */
							// String unit = "";
							// if (j == 0) {
							// unit = "mv";
							// } else if (j == 2) {
							// unit = "ms";
							// } else if (j == 3 || j == 4) {
							// unit = "℃";
							// } else if (j == 5 || j == 6) {
							// unit = "mg/l";
							// }
							DataRecord record = new DataRecord();
							record.setChannelNO(j);// 通道序号，并不是ID号
							// record.setRawData(f);// 原始值
							// record.setDataUnit(unit);
							record.setCheckItemId(checkItemIds.get(j).toString());
							record.setProcessedData(f);//
							record.setEquipmentId(equipmentId);// 是设备地址，需要转化为设备ID
							record.setAcquisitionTime(sDate);
							record.setReportingTime(sDate);
							records.add(record);
						}
						byte[] content = new byte[] { (byte) 0xCB, (byte) 0xCE };
						response = buildResponsePackage((byte) 0x47, device, content);
						service.addBatch(records);

						logger.info("-----------0x47 接收设备上报数据并返回----------");
					} else if (flag == 0x29) {

						logger.info("-----------0x29 ----------");

					} else if (flag == 0x30) {// 索要数据
						logger.info("-----------0x30 ----------");

					} else if (flag == 0x31) {// 状态数据上报
						logger.info("-----------0x31 ----------");

					} else if (flag == 0x32) {// 警情上报
						logger.info("-----------0x32 ----------");

					}
					if (response != null) {
						AtomicBoolean isWriting = isWritingMap.get(client);
						if (isWriting == null) {
							isWriting = new AtomicBoolean(false);
							isWritingMap.put(client, isWriting);
						}
						while (isWriting.get()) {
							Thread.sleep(5);
						}
						isWriting.set(true);
						client.register(selector, SelectionKey.OP_WRITE, response);
					} else {
						logger.info("-----------服务端没有可应答的数据！----------");
					}
				}
			} catch (Exception e) {
				try {
					SelectionKey selectionKey = client.keyFor(selector);
					if (selectionKey != null) {
						selectionKey.cancel();
					}
					logger.warn("客户端[" + client + "]关闭了连接,原因[" + e + "]");
					if (client != null) {
						client.close();
					}
				} catch (IOException e1) {
					logger.error("客户端[" + client + "]关闭异常" + e1.getMessage());
				}
			} finally {
				AtomicBoolean isReading = isReadingMap.get(client);
				isReading.set(false);
			}
		}

		/**
		 * 对比数据CRC结果
		 * 
		 * @param crcCheckData
		 *            待校验的数据
		 * @param data
		 *            原来去包头（不包含前四位）的数据包。
		 * @return 一致则true,否则false
		 */
		private boolean checkCRCVal(final ByteBuffer crcCheckData, final ByteBuffer data) {
			int total = data.array().length;
			String t1 = DataFormater.byteToHex(data.get(total - 4));
			String t2 = DataFormater.byteToHex(data.get(total - 3));
			if (t1.length() < 2) {
				t1 = "0" + t1;
			}
			if (t2.length() < 2) {
				t2 = "0" + t2;
			}
			String originCrc = t1 + t2;
			String crc = CRC16.getCrcHex(crcCheckData.array());
			logger.info("原数据CRC值=" + originCrc + ",计算CRC结果 :[" + crc + "]");
			if (crc.equalsIgnoreCase(originCrc)) {
				return true;
			}
			return false;
		}

		/**
		 * 获取设备在址
		 * 
		 * @param data
		 *            去掉包头（不包含前四位）的数据包
		 * @return 返回设备地址四位byte
		 */
		private byte[] getDevice(ByteBuffer data) {
			byte[] device = new byte[4];
			System.arraycopy(data.array(), 3, device, 0, 4);
			return device;
		}

		/**
		 * 获取一个数据指定长度的内容段
		 * 
		 * @param data
		 *            原数据
		 * @param posIndex
		 *            起如位置
		 * @param len
		 *            截取的长度
		 * @return 定长度的内容段 byte[]
		 */
		private byte[] getData(ByteBuffer data, int posIndex, int len) {
			byte[] b = new byte[len];
			System.arraycopy(data.array(), posIndex, b, 0, len);
			return b;
		}

		/**
		 * 构建应答设备登录（0x41）的数据包
		 * 
		 * @param data
		 *            去掉包头（不包含前四位）的数据包
		 * @return 完整的数据
		 */
		private byte[] buildRegisterPackage(ByteBuffer data) {
			byte[] device = getDevice(data);
			byte[] content = new byte[] { 0x01 };
			return buildResponsePackage((byte) 0x41, device, content);
		}

		/**
		 * 构建应答数据包。
		 * 
		 * @param direction
		 *            标识位，即功能代码
		 * @param device
		 *            设备地址字节数组
		 * @param content
		 *            要应答数据内容
		 * @return
		 */
		private byte[] buildResponsePackage(byte direction, byte[] device, byte[] content) {

			// 响应数据包总长度=[帧头2] +[方向1]+[帧长度位1]＋[帧代号1]+[功能码2]+ [地址段4] + [数据n]
			// +[CRC校验2]+[包尾2]
			final int dlen = device.length;
			final int len = 7 + dlen + content.length + 2 + 2;

			byte[] response = new byte[len];
			response[0] = (byte) 0x88;
			response[1] = (byte) 0xFB;
			response[2] = (byte) 0xAF;

			// 长度值 = [帧代号1]+[功能码2] + [地址段m] + [数据n] +[CRC校验2]
			response[3] = (byte) (len - 6);// 数据包有效长度值，从长度位到负载的
			response[4] = direction;
			response[5] = (byte) 0x61;
			response[6] = (byte) 0x00;
			System.arraycopy(device, 0, response, 7, dlen);// 设备在址
			System.arraycopy(content, 0, response, 7 + dlen, content.length);// 应答内容
			byte[] resCrc = new byte[len - 7];
			System.arraycopy(response, 3, resCrc, 0, len - 7);
			byte[] nR = CRC16.getCrcBytes(resCrc);
			System.arraycopy(nR, 0, response, len - 4, 2);

			// 包尾
			response[len - 2] = (byte) 0xFC;
			response[len - 1] = (byte) 0xFC;
			return response;
		}
	}

	public static void main(String[] args) {
		logger.info("--------------->Enter to main method");
		SocketServer server = new SocketServer();
		server.start();
		logger.info("--------------->Server already started!");
	}

}
