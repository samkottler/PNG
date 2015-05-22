import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class PNG{
    private FileOutputStream out;
    private final int WIDTH, HEIGHT;
    private byte[] colors;
    public PNG(String file, int width, int height){
        WIDTH = width;
        HEIGHT = height;
        colors = new byte[WIDTH*HEIGHT*3 + HEIGHT];
        try{
            out = new FileOutputStream(file);
        }
        catch(IOException e){
            System.out.println("Error: " + e.getMessage()); 
        }
        
        
        PNGChunk IHDR = new PNGChunk(13,"IHDR");
        IHDR.addData(new byte[]{(byte)((width>>24)&0xff),
                (byte)((width>>16)&0xff),(byte)((width>>8)&0xff),(byte)((width)&0xff),
                (byte)((height>>24)&0xff),(byte)((height>>16)&0xff),
                (byte)((height>>8)&0xff),(byte)((height)&0xff),8,2,0,0,0});
        byte[] head = new byte[33];
        head[0] = (byte)'\211';
        head[1] = 'P';
        head[2] = 'N';
        head[3] = 'G';
        head[4] = '\r';
        head[5] = '\n';
        head[6] = '\32';
        head[7] = '\n';
        
        byte[] IHDRChunk = IHDR.getChunk();
        for(int i = 0; i<IHDRChunk.length; i++){
            head [8+i] = IHDRChunk[i];
        }
        try{
            out.write(head);
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    
    public void setPoint(int x, int y, int rgb){
        int index = colors.length - (x+WIDTH*y)*3 - y - 1;
        colors[index] = (byte)(rgb&0xff);
        colors[index - 1] = (byte)((rgb>>8)&0xff);
        colors[index - 2] = (byte)((rgb>>16)&0xff);
    }
    public void close(){
                  
        Deflater def = new Deflater();
        def.setInput(colors);
        def.finish();
        int b = def.deflate(colors);
        def.end();
        PNGChunk IDAT = new PNGChunk(b,"IDAT");
        IDAT.addData(colors);
        
        PNGChunk IEND = new PNGChunk(0,"IEND");
        IEND.addData(new byte[]{});
        try{
            out.write(IDAT.getChunk());
            out.write(IEND.getChunk());
            out.close();
        }
        catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
        
    }
}
