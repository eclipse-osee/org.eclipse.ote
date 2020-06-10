/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.io.mux;

import org.eclipse.osee.framework.jdk.core.util.ByteUtil;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;

/**
 * @author Andrew M. Finkbeiner
 * @author Ken J. Aguilar
 * @author Michael P. Masterson
 */
public class MuxHeader implements IMessageHeader {

	public static final int MUX_HEADER_BYTE_SIZE = 15;

	public static final int muxheaderLayout[][] =
	{ { 0, 0, 7 }, 	// MUX PORT NUMBER
		{1, 0, 4}, // REMOTE TERMINAL ADDRESS 1
		{1, 5, 5}, // TRANSMIT RECEIVE FLAG 1
		{1, 6, 10}, // SUBADDRESS 1
		{1, 11, 15}, // WORD COUNT 1
		{3, 0, 4}, // REMOTE TERMINAL ADDRESS 2
		{3, 5, 5}, // TRANSMIT RECEIVE FLAG 2
		{3, 6, 10}, // SUBADDRESS 2
		{3, 11, 15}, // WORD COUNT 2
		{5, 0, 4}, // STATUS RT ADDRESS 1
		{5, 5, 5}, // MESSAGE ERROR FLAG 1
		{5, 6, 6}, // INSTRUMENTATION FLAG 1
		{5, 7, 7}, // SERVICE REQUEST FLAG 1
		{5, 11, 11}, // BROADCAST COMMAND RECEIVED FLAG 1
		{5, 12, 12}, // BUSY FLAG 1
		{5, 13, 13}, // SUBSYSTEM FLAG 1
		{5, 14, 14}, // DYNAMIC BUS ACCEPTANCE FLAG 1
		{5, 15, 15}, // TERMINAL FLAG 1
		{7, 0, 4}, // STATUS RT ADDRESS 2
		{7, 5, 5}, // MESSAGE ERROR FLAG 2
		{7, 6, 6}, // INSTRUMENTATION FLAG 2
		{7, 7, 7}, // SERVICE REQUEST FLAG 2
		{7, 11, 11}, // BROADCAST COMMAND RECEIVED FLAG 2
		{7, 12, 12}, // BUSY FLAG 2
		{7, 13, 13}, // SUBSYSTEM FLAG 2
		{7, 14, 14}, // DYNAMIC BUS ACCEPTANCE FLAG 2
		{7, 15, 15}, // TERMINAL FLAG 2
		{9, 0, 0}, // PRIMARY RT SIMULATE FLAG
		{9, 1, 1}, // SECONDARY RT SIMULATE FLAG
		{10, 0, 0}, // NO RESPONSE FLAG
		{10, 1, 1}, // MODE CODE FLAG
		{10, 2, 2}, // RT-RT FLAG
		{10, 3, 3}, // BROADCAST FLAG
		{10, 4, 4}, // PRIMARY SECONDARY FLAG (0=A, 1=B)
		{11, 0, 31} // 32-BIT MICROSECOND TIME TAG
	};

	private final char[] nameBuffer = "CH#_##X##".toCharArray();
	private final HeaderData headerData;
	public final IntegerElement MUX_PORT_NUMBER;

	public final IntegerElement COMMAND_1;
	public final IntegerElement REMOTE_TERMINAL_1;
	public final IntegerElement DIRECTION_1;
	public final IntegerElement SUBADDRESS_1;
	public final IntegerElement WORD_COUNT_1;

	public final IntegerElement COMMAND_2;
	public final IntegerElement REMOTE_TERMINAL_2;
	public final IntegerElement DIRECTION_2;
	public final IntegerElement SUBADDRESS_2;
	public final IntegerElement WORD_COUNT_2;

	public final IntegerElement STATUS_1;
	public final BooleanElement MESSAGE_ERROR_FLAG_1;
	public final BooleanElement INSTRUMENTATION_FLAG_1;
	public final BooleanElement SERVICE_REQ_FLAG_1;
	public final BooleanElement BROADCAST_CMD_RECV_1;
	public final BooleanElement BUSY_1;
	public final BooleanElement SUBSYSTEM_1;
	public final BooleanElement DYNAMIC_BUS_ACCEPT_FLAG_1;
	public final BooleanElement TERMINAL_FLAG_1;

	public final IntegerElement STATUS_2;
	public final BooleanElement MESSAGE_ERROR_FLAG_2;
	public final BooleanElement INSTRUMENTATION_FLAG_2;
	public final BooleanElement SERVICE_REQ_FLAG_2;
	public final BooleanElement BROADCAST_CMD_RECV_2;
	public final BooleanElement BUSY_2;
	public final BooleanElement SUBSYSTEM_2;
	public final BooleanElement DYNAMIC_BUS_ACCEPT_FLAG_2;
	public final BooleanElement TERMINAL_FLAG_2;

	public final BooleanElement PRIMARY_RT_SIMULATE_FLAG;
	public final BooleanElement SECONDARY_RT_SIMULATE_FLAG;
	public final BooleanElement NO_RESPONSE_FLAG;
	public final BooleanElement MODE_CODE_FLAG;
	public final BooleanElement RT_TO_RT_FLAG;
	public final BooleanElement BROADCAST_FLAG;
	public final BooleanElement PRIMARY_SECONDARY_FLAG;
	public final IntegerElement TIMESTAMP;

	public MuxHeader(MemoryResource data) {
		this(null, data);
	}

	public MuxHeader(Message msg, MemoryResource data) {
		headerData = new HeaderData("MUX_HEADER", data);

		Object[] path = new Object[]{(msg == null ? "message" : msg.getClass().getName()), "HEADER(MUX)"};
		MUX_PORT_NUMBER = new IntegerElement(msg, "MUX_PORT_NUMBER", headerData, 0, 0, 7);
		MUX_PORT_NUMBER.addPath(path);
		COMMAND_1 = new IntegerElement(msg, "COMMAND_1", headerData, 1, 0, 15);
		COMMAND_1.addPath(path);
		REMOTE_TERMINAL_1 = new IntegerElement(msg, "REMOTE_TERMINAL_1", headerData, 1, 0, 4);
		REMOTE_TERMINAL_1.addPath(path);
		DIRECTION_1 = new IntegerElement(msg, "DIRECTION_1", headerData, 1, 5, 5);
		DIRECTION_1.addPath(path);
		SUBADDRESS_1 = new IntegerElement(msg, "SUBADDRESS_1", headerData, 1, 6, 10);
		SUBADDRESS_1.addPath(path);
		WORD_COUNT_1 = new IntegerElement(msg, "WORD_COUNT_1", headerData, 1, 11, 15);
		WORD_COUNT_1.addPath(path);

		COMMAND_2 = new IntegerElement(msg, "COMMAND_2", headerData, 3, 0, 15);
		COMMAND_2.addPath(path);
		REMOTE_TERMINAL_2 = new IntegerElement(msg, "REMOTE_TERMINAL_2", headerData, 3, 0, 4);
		REMOTE_TERMINAL_2.addPath(path);
		DIRECTION_2 = new IntegerElement(msg, "DIRECTION_2", headerData, 3, 5, 5);
		DIRECTION_2.addPath(path);
		SUBADDRESS_2 = new IntegerElement(msg, "SUBADDRESS_2", headerData, 3, 6, 10);
		SUBADDRESS_2.addPath(path);
		WORD_COUNT_2 = new IntegerElement(msg, "WORD_COUNT_2", headerData, 3, 11, 15);
		WORD_COUNT_2.addPath(path);

		STATUS_1 = new IntegerElement(msg, "STATUS_1", headerData, 5, 0, 15);
		STATUS_1.addPath(path);
		MESSAGE_ERROR_FLAG_1 = new BooleanElement(msg, "MESSAGE_ERROR_FLAG_1", headerData, 5, 5, 5);
		MESSAGE_ERROR_FLAG_1.addPath(path);
		INSTRUMENTATION_FLAG_1 = new BooleanElement(msg, "INSTRUMENTATION_FLAG_1", headerData, 5, 6, 6);
		INSTRUMENTATION_FLAG_1.addPath(path);
		SERVICE_REQ_FLAG_1 = new BooleanElement(msg, "SERVICE_REQ_FLAG_1", headerData, 5, 7, 7);
		SERVICE_REQ_FLAG_1.addPath(path);
		BROADCAST_CMD_RECV_1 = new BooleanElement(msg, "BROADCAST_CMD_RECV_1", headerData, 5, 11, 11);
		BROADCAST_CMD_RECV_1.addPath(path);
		BUSY_1 = new BooleanElement(msg, "BUSY_1", headerData, 5, 12, 12);
		BUSY_1.addPath(path);
		SUBSYSTEM_1 = new BooleanElement(msg, "SUBSYSTEM_1", headerData, 5, 13, 13);
		SUBSYSTEM_1.addPath(path);
		DYNAMIC_BUS_ACCEPT_FLAG_1 = new BooleanElement(msg, "DYNAMIC_BUS_ACCEPT_FLAG_1", headerData, 5, 14, 14);
		DYNAMIC_BUS_ACCEPT_FLAG_1.addPath(path);
		TERMINAL_FLAG_1 = new BooleanElement(msg, "TERMINAL_FLAG_1", headerData, 5, 15, 15);
		TERMINAL_FLAG_1.addPath(path);

		STATUS_2 = new IntegerElement(msg, "STATUS_2", headerData, 7, 0, 15);
		STATUS_2.addPath(path);
		MESSAGE_ERROR_FLAG_2 = new BooleanElement(msg, "MESSAGE_ERROR_FLAG_2", headerData, 7, 5, 5);
		MESSAGE_ERROR_FLAG_2.addPath(path);
		INSTRUMENTATION_FLAG_2 = new BooleanElement(msg, "INSTRUMENTATION_FLAG_2", headerData, 7, 6, 6);
		INSTRUMENTATION_FLAG_2.addPath(path);
		SERVICE_REQ_FLAG_2 = new BooleanElement(msg, "SERVICE_REQ_FLAG_2", headerData, 7, 7, 7);
		SERVICE_REQ_FLAG_2.addPath(path);
		BROADCAST_CMD_RECV_2 = new BooleanElement(msg, "BROADCAST_CMD_RECV_2", headerData, 7, 11, 11);
		BROADCAST_CMD_RECV_2.addPath(path);
		BUSY_2 = new BooleanElement(msg, "BUSY_2", headerData, 7, 12, 12);
		BUSY_2.addPath(path);
		SUBSYSTEM_2 = new BooleanElement(msg, "SUBSYSTEM_2", headerData, 7, 13, 13);
		SUBSYSTEM_2.addPath(path);
		DYNAMIC_BUS_ACCEPT_FLAG_2 = new BooleanElement(msg, "DYNAMIC_BUS_ACCEPT_FLAG_2", headerData, 7, 14, 14);
		DYNAMIC_BUS_ACCEPT_FLAG_2.addPath(path);
		TERMINAL_FLAG_2 = new BooleanElement(msg, "TERMINAL_FLAG_2", headerData, 7, 15, 15);
		TERMINAL_FLAG_2.addPath(path);

		PRIMARY_RT_SIMULATE_FLAG = new BooleanElement(msg, "PRIMARY_RT_SIMULATE_FLAG", headerData, 9, 0, 0);
		PRIMARY_RT_SIMULATE_FLAG.addPath(path);
		SECONDARY_RT_SIMULATE_FLAG = new BooleanElement(msg, "SECONDARY_RT_SIMULATE_FLAG", headerData, 9, 1, 1);
		SECONDARY_RT_SIMULATE_FLAG.addPath(path);
		NO_RESPONSE_FLAG = new BooleanElement(msg, "NO_RESPONSE_FLAG", headerData, 10, 0, 0);
		NO_RESPONSE_FLAG.addPath(path);
		MODE_CODE_FLAG = new BooleanElement(msg, "MODE_CODE_FLAG", headerData, 10, 1, 1);
		MODE_CODE_FLAG.addPath(path);
		RT_TO_RT_FLAG = new BooleanElement(msg, "RT_TO_RT_FLAG", headerData, 10, 2, 2);
		RT_TO_RT_FLAG.addPath(path);
		BROADCAST_FLAG = new BooleanElement(msg, "BROADCAST_FLAG", headerData, 10, 3, 3);
		BROADCAST_FLAG.addPath(path);
		PRIMARY_SECONDARY_FLAG = new BooleanElement(msg, "PRIMARY_SECONDARY_FLAG", headerData, 10, 4, 4);
		PRIMARY_SECONDARY_FLAG.addPath(path);
		TIMESTAMP = new IntegerElement(msg, "TIMESTAMP", headerData, 11, 0, 31);
		TIMESTAMP.addPath(path);

		decodeHeaderInfo();
	}

	public String resolveName() {
		decodeHeaderInfo();
		return new String(nameBuffer);
	}

	public byte[] getData() {
		return headerData.toByteArray();
	}

	private void decodeHeaderInfo() {

		nameBuffer[2] = (char) (MUX_PORT_NUMBER.getValue() + 48);
		getRt(REMOTE_TERMINAL_1.getValue());
		nameBuffer[6] = DIRECTION_1.getValue() == 1 ? 'T' : 'R';
		getSa(SUBADDRESS_1.getValue());

	}

	public void putNameIntoBuffer(StringBuilder buffer) {
		decodeHeaderInfo();
		buffer.append(nameBuffer);
	}

	public String getCommandWord2String() {
		final char transmitReceive2 = DIRECTION_2.getValue() == 1 ? 'T' : 'R';

		return String.format("CH%d_%02d%c%02d", MUX_PORT_NUMBER.getValue(), REMOTE_TERMINAL_2.getValue(),
				transmitReceive2, SUBADDRESS_2.getValue());
	}

	private void getRt(int i) {
		nameBuffer[4] = (char) ((i / 10) + 48);
		nameBuffer[5] = (char) ((i % 10) + 48);
	}

	private void getSa(int i) {
		nameBuffer[7] = (char) ((i / 10) + 48);
		nameBuffer[8] = (char) ((i % 10) + 48);
	}


	@Override
	public String getMessageName() {
		decodeHeaderInfo();
		return new String(nameBuffer);
	}

	public int getDataByteSize() {
		return getWordCount1() * 2;
	}


	public int getWordCount1() {
		int retVal = WORD_COUNT_1.getValue();
		return (retVal == 0 ? 32 : retVal);
	}

	public boolean isTransmitReceiveFlag2Enabled() {
		return DIRECTION_2.getValue() == 1;
	}

	public int getWordCount2() {
		int wc = WORD_COUNT_2.getValue();
		return wc == 0 ? 32 : wc;
	}

	public int getPrimarySecondaryFlag() {
		 return PRIMARY_SECONDARY_FLAG.getValue() ? 1 : 0;
	 }

	 public String toXml() {
		 StringBuilder builder = new StringBuilder();
		 builder.append(String.format("<MuxHeaderInfo " +
				 "dataByteSize=\"%d\" muxPortNum=\"%d\" remoteTermAdd1=\"%d\" transmitRcvFlg1Enabled=\"%b\" "+
				 "subAddress1=\"%d\" wordCount1=\"%d\" remoteTerminalAddress2=\"%d\" transmitRcvFlg2Enabled=\"%b\" "+
				 "subAddress2=\"%d\" wordCount2=\"%d\" statusRtAddress1=\"%d\" msgErrorFlagEnabled=\"%b\" "+
				 "instrumentationFlagEnabled=\"%b\" serviceRequestFlagEnabled=\"%b\" broadcastCmdRcvFlag1Enabled=\"%b\" busyFlag1Enabled=\"%b\" "+
				 "subsystemFlag1Enabled=\"%b\" dynamicBusAcceptanceFlag1Enabled=\"%b\" terminalFlag1Enabled=\"%b\" statusRtAddress2Enabled=\"%b\" "+
				 "messageErrorFlag2Enabled=\"%b\" instrumentationFlag2Enabled=\"%b\" serviceRequestFlag2Enabled=\"%b\" broadcastCommandRecievedFlag2Enabled=\"%b\" "+
				 "busyFlag2Enabled=\"%b\" subsystemFlag2Enabled=\"%b\" dynamicBusAcceptanceFlag2enabled=\"%b\" terminalFlag2Enabled=\"%b\" "+
				 "primaryRtSimulateFlagEnabled=\"%b\" secondaryRtSimulateFlagEnabled=\"%b\" NoResponseFlagEnabled=\"%b\" ModeCodeFlagEnabled=\"%b\" "+
				 "RtRtFlagEnabled=\"%b\" BroadcastFlagEnabled=\"%b\" PrimarySecondaryFlag=\"%d\" TimeTag=\"%d\" >\n",
				 getDataByteSize() ,
				 MUX_PORT_NUMBER.getValue(),
				 REMOTE_TERMINAL_1.getValue() ,
				 DIRECTION_1.getValue() == 1,
				 SUBADDRESS_1.getValue(),
				 getWordCount1() ,
				 REMOTE_TERMINAL_2.getValue() ,
				 isTransmitReceiveFlag2Enabled() ,
				 SUBADDRESS_2.getValue() ,
				 getWordCount2() ,
				 STATUS_1.getValue() ,
				 MESSAGE_ERROR_FLAG_1.getValue(),
				 INSTRUMENTATION_FLAG_1.getValue() ,
				 SERVICE_REQ_FLAG_1.getValue() ,
				 BROADCAST_CMD_RECV_1.getValue() ,
				 BUSY_1.getValue() ,
				 SUBSYSTEM_1.getValue() ,
				 DYNAMIC_BUS_ACCEPT_FLAG_1.getValue() ,
				 TERMINAL_FLAG_1.getValue() ,
				 STATUS_2.getValue() != 0 ,
				 MESSAGE_ERROR_FLAG_2.getValue() ,
				 INSTRUMENTATION_FLAG_2.getValue() ,
				 SERVICE_REQ_FLAG_2.getValue() ,
				 BROADCAST_CMD_RECV_2.getValue(),
				 BUSY_2.getValue() ,
				 SUBSYSTEM_2.getValue() ,
				 DYNAMIC_BUS_ACCEPT_FLAG_2.getValue(),
				 TERMINAL_FLAG_2.getValue() ,
				 PRIMARY_RT_SIMULATE_FLAG.getValue() ,
				 SECONDARY_RT_SIMULATE_FLAG.getValue() ,
				 NO_RESPONSE_FLAG.getValue() ,
				 MODE_CODE_FLAG.getValue() ,
				 RT_TO_RT_FLAG.getValue() ,
				 BROADCAST_FLAG.getValue() ,
				 getPrimarySecondaryFlag() ,
				 TIMESTAMP.getValue()
		 ));
		 ByteUtil.printByteDump(builder, this.getData(), 0, MUX_HEADER_BYTE_SIZE, 16);
		 builder.append("\n</MuxHeaderInfo>");
		 return builder.toString();
	 }

	 public Element[] getElements() {
		 return new Element[] {
				 MUX_PORT_NUMBER,
				 REMOTE_TERMINAL_1,
				 DIRECTION_1,
				 SUBADDRESS_1,
				 WORD_COUNT_1,
				 REMOTE_TERMINAL_2,
				 DIRECTION_2,
				 SUBADDRESS_2,
				 WORD_COUNT_2,
				 STATUS_1,
				 MESSAGE_ERROR_FLAG_1,
				 INSTRUMENTATION_FLAG_1,
				 SERVICE_REQ_FLAG_1,
				 BROADCAST_CMD_RECV_1,
				 BUSY_1,
				 SUBSYSTEM_1,
				 DYNAMIC_BUS_ACCEPT_FLAG_1,
				 TERMINAL_FLAG_1,
				 STATUS_2,
				 MESSAGE_ERROR_FLAG_2,
				 INSTRUMENTATION_FLAG_2,
				 SERVICE_REQ_FLAG_2,
				 BROADCAST_CMD_RECV_2,
				 BUSY_2,
				 SUBSYSTEM_2,
				 DYNAMIC_BUS_ACCEPT_FLAG_2,
				 TERMINAL_FLAG_2,
				 PRIMARY_RT_SIMULATE_FLAG,
				 SECONDARY_RT_SIMULATE_FLAG,
				 NO_RESPONSE_FLAG,
				 MODE_CODE_FLAG,
				 RT_TO_RT_FLAG,
				 BROADCAST_FLAG,
				 PRIMARY_SECONDARY_FLAG,
				 TIMESTAMP
		 };
	 }

	 public int getHeaderSize() {
		 return MUX_HEADER_BYTE_SIZE;
	 }

	 public void setNewBackingBuffer(byte[] data) {
		 headerData.setNewBackingBuffer(data);
		 decodeHeaderInfo();
	 }

}
