package com.moonspringstudio.mTouchPad;

/**
 * Created by Eric on 14-6-9.
 */
public class TransferProtocal {
    public static final byte[] DataHeader = {(byte) 0xEC, (byte) 0xEF};

    enum CommandType {
        WhoAmIFromClient((byte) 0xEE),
        WhoAmIFromServer((byte) 0xEF),
        Connect((byte) 0xED),
        Disconnect((byte) 0xEC),
        MouseLeftButtonDown((byte) 0x01),
        MouseLeftButtonUp((byte) 0x02),
        MouseRightButtonDown((byte) 0x03),
        MouseRightButtonUp((byte) 0x04),
        MouseMove((byte) 0x05),
        MouseDrag((byte) 0x06),
        MouseLeftButtonClick((byte) 0x07),
        MouseRightButtonClick((byte) 0x08);
        private byte value;

        private CommandType(byte value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
