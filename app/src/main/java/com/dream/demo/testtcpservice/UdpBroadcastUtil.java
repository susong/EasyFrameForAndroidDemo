package com.dream.demo.testtcpservice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UdpBroadcastUtil {

	public static int RECEIVE_PORT = 8802;
	public static int SEND_PORT = 8801;
	private static UdpReceiverThread mReceiver = null;
	private static MainActivity activity;
	public static void registerBroadcastReceiver(MainActivity activity) {
		try {
			UdpBroadcastUtil.activity = activity;
			if (mReceiver != null) {
				mReceiver.stopSelf();
				mReceiver = null;
			}

			mReceiver = new UdpReceiverThread();
			mReceiver.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public UdpBroadcastUtil(MainActivity activity){

	}
	public static void sendBroadcast1(final String text) {
		new Thread(new Runnable() {
			public void run() {

				try {
					byte[] msg = text.getBytes("utf-8");
					InetAddress inetAddr;
					inetAddr = InetAddress.getByName("255.255.255.255");

					DatagramSocket client = new DatagramSocket();

					DatagramPacket sendPack = new DatagramPacket(msg,
																 msg.length, inetAddr, SEND_PORT);
					client.send(sendPack);

					System.out.println("Client send msg complete:" + SEND_PORT);
					//Toast.makeText(activity, text, 0).show();
					client.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	public static void sendBroadcast3(final String text) {
		new Thread(new Runnable() {
			public void run() {

				try {
					byte[] msg = text.getBytes("utf-8");
					InetAddress inetAddr;
					inetAddr = InetAddress.getByName("255.255.255.255");

					DatagramSocket client = new DatagramSocket();

					DatagramPacket sendPack = new DatagramPacket(msg,
																 msg.length, inetAddr, RECEIVE_PORT);
					client.send(sendPack);
					System.out.println("Client send msg complete:" + SEND_PORT);
					client.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void sendBroadcast(final String json) {
		new Thread(new Runnable() {
			public void run() {
				MulticastSocket sendSocket = null;
				try {
					InetAddress inetAddress = InetAddress
							.getByName("255.255.255.255");
					sendSocket = new MulticastSocket(SEND_PORT);
					DatagramPacket dataPacket = null;
					byte[] data = json.getBytes();
					dataPacket = new DatagramPacket(data, data.length,
													inetAddress, SEND_PORT);
					for (int i = 0; i < dataPacket.getLength(); i++) {
						try {
							sendSocket.send(dataPacket);
							Thread.sleep(1000 * 5);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (sendSocket != null) {
							sendSocket.close();
							sendSocket = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private static String ipString = "";
	private static class UdpReceiverThread extends Thread {
		private boolean mRunning = false;
		private MulticastSocket receiveSocket = null;
		@Override
		public void run() {
			mRunning = true;
			try {
				while (mRunning) {
					try {
						Log.i("broadcast", "Receiver run...:" + RECEIVE_PORT);
						byte[] buf = new byte[1024];
						receiveSocket = new MulticastSocket(RECEIVE_PORT);
						receiveSocket.setSoTimeout(1000 * 60 * 10);
						receiveSocket.setBroadcast(true);

						DatagramPacket receivedPkt = new DatagramPacket(buf,
																		buf.length);

						receiveSocket.receive(receivedPkt);
						String receivedString = new String(
								receivedPkt.getData(), receivedPkt.getOffset(),
								receivedPkt.getLength(),"utf-8");

						Message msg = new Message();
						msg.obj = receivedString;
						handler.sendMessage(msg);
						//							}

						//						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(50);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (receiveSocket != null) {
						receiveSocket.close();
						receiveSocket = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void stopSelf() {
			try {
				Log.i("broadcast", "top recevie");
				if (receiveSocket != null) {
					receiveSocket.disconnect();
					receiveSocket = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mRunning = false;
				interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//String ipString = Util.getIP();
			JSONObject jsonObject;// = new JSONObject(msg.obj.toString());
			String typeString = "";
			try {
				jsonObject = new JSONObject(msg.obj.toString());
				typeString = jsonObject.getString("type");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//REQUEST_TCP
			if (typeString.equals("REQUEST_TCP")) {
				sendUDP();
			}
			super.handleMessage(msg);

		}
	};

	public static void stopReceive() {
		if (mReceiver != null) {
			mReceiver.stopSelf();
		}
	}
	private static void sendUDP() {
		String ips = Util.getIP();
		IPS ips2 = new IPS();
		ips2.setIp(ips);
		String jsonString = JSON.toJSONString(ips2);
		sendBroadcast(jsonString);
	}
}
