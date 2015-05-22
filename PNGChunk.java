class PNGChunk{
    private long[] crcTable = new long[256];
    private byte[] chunk;
    public PNGChunk(int size, String type){
        chunk = new byte[12+size];
        //System.out.println(size);
        chunk[0] = (byte)((size>>24) & 0xff);
        chunk[1] = (byte)((size>>16) & 0xff);
        chunk[2] = (byte)((size>>8) & 0xff);
        chunk[3] = (byte)((size) & 0xff);
        chunk[4] = (byte)type.charAt(0);
        chunk[5] = (byte)type.charAt(1);
        chunk[6] = (byte)type.charAt(2);
        chunk[7] = (byte)type.charAt(3);
    }
    
    public void addData(byte[] dat){
        makeCRCTable();
        for (int i = 0; i<chunk.length-12; i++){
            chunk[i+8] = dat[i];
        }
        char[] chars = new char[chunk.length-8];
        for (int i = 0; i<chars.length; i++){
            chars[i] = (char)chunk[4+i];
        }
        long c = crc(chars);
        chunk[chunk.length-4] = (byte)((c>>24)&0xff);
        chunk[chunk.length-3] = (byte)((c>>16)&0xff);
        chunk[chunk.length-2] = (byte)((c>>8)&0xff);
        chunk[chunk.length-1] = (byte)((c)&0xff);
    }
    
    public byte[] getChunk(){
        return chunk;
    }
    
    private void makeCRCTable(){
        for (int n = 0; n<256; n++){
            long c = (long) n;
            for(int k = 0; k<8;k++){
                if ((c&1) == 1) c = 0xedb88320L ^ (c>>1);
                else c = c>>1;
            }
            crcTable[n] = c;
        }
    }
    private long updateCRC(long crc, char[] buf){
        long c = crc;
        for (int i = 0; i<buf.length; i++){
            c = crcTable[(int)((c^buf[i])&0xff)] ^ (c>>8);
        }
        return c;
    }
    private long crc(char[] buf){
        return updateCRC(0xffffffffL, buf) ^ 0xffffffffL;
    }
}
