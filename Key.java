package RC;
import java.io.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Key {
    private  int w;
    private  int r;
    private int b;
    private int []p;
    private int []q;
    private ArrayList<int[]> keys;
    private ArrayList<int[]> s;

    public ArrayList<int[]> getKeys(){return keys;}
    public ArrayList<int[]> getS(){return s;}
    public int getW(){return w;}
    public int getR(){return r;}

    public Key(int w, int r, String key){
        this.w=w/2;
        this.r=r;
        this.b=key.length();
        this.generationConst();
        this.generationKeys(key);
        this.generationS();
        this.swap();
    }

    public Key(int w, int r) throws IOException{
        byte [] k= new byte[16];
        FileOutputStream fileOut = new FileOutputStream("key.txt");
        for(int i=0; i<k.length;i++){
           k[i] = (byte) (Math.random()*127);
           fileOut.write(k[i]);
        }
        String key = new String(k);
       // System.out.println(key);
        this.w=w/2;
        this.r=r;
        this.b=k.length;
        this.generationConst();
        this.generationKeys(key);
        this.generationS();
        this.swap();
    }

    private void generationConst(){
        if(w==16){
            p=new int []{1,0,1,1,0,1,1,1,1,1,1,0,0,0,0,1};
            q=new int []{1,0,0,1,1,1,1,0,0,0,1,1,0,1,1,1};
        }
        if(w==32){
            p=new int []{1,0,1,1,0,1,1,1,1,1,1,0,0,0,0,1,0,1,0,1,0,0,0,1,0,1,1,0,0,0,1,1};
            q=new int []{1,0,0,1,1,1,1,0,0,0,1,1,0,1,1,1,0,1,1,1,1,0,0,1,1,0,1,1,1,0,0,1};
        }
        if(w==64){
            p=new int []{1,0,1,1,0,1,1,1,1,1,1,0,0,0,0,1,0,1,0,1,0,0,0,1,0,1,1,0,0,0,1,0,1,0,0,0,1,0,1,0,1,1,1,0,1,1,0,1,0,0,1,0,1,0,1,0,0,1,1,0,1,0,1,1};
            q=new int []{1,0,0,1,1,1,1,0,0,0,1,1,0,1,1,1,0,1,1,1,1,0,0,1,1,0,1,1,1,0,0,1,0,1,1,1,1,1,1,1,0,1,0,0,1,0,1,0,0,1,1,1,1,1,0,0,0,0,0,1,0,1,0,1};
        }
    }
    private void generationKeys(String key){
        keys = new ArrayList<>();
        byte[] bytes = key.getBytes();
        int [] bit = new int[8*b];
        for (int i=0; i<b; i++){
            int by = (byte)bytes[i];
            int [] tmp = new int[8];
            for (int j=0; j<8; j++){
                tmp[j]=by%2;
                by=(by-by%2)/2;
            }
            for (int j=0; j<8; j++) {
                bit[i*8+j]=tmp[7-j];
            }
        }
        int length = w/8;
        int c = b/length;
        if((b/length!=0)||(c==0)) c++;
        int begin = 0;
        int end = w;
        for (int i = 0; i < c; i++) {
            int[] k = new int[w];
            int count = 0;
            for (int j = begin; j < end; j++) {
                if (j >= bit.length)
                    k[count] = 0;
                else
                    k[count] = bit[j];
                count++;
            }
            begin += w;
            end += w;
            keys.add(k);
        }
//        for (int i = 0; i < keys.size(); i++) {
//            for (int j = 0; j < keys.get(i).length; j++) {
//                if(j%8==0) System.out.print(" ");
//                System.out.print(keys.get(i)[j]);
//            }
//            System.out.println();
//        }
    }

    private void generationS(){
        s = new ArrayList<>();
        s.add(0,p);
        for (int i = 1; i < 2 * (r + 1); i++) {
            int[] k =sum(s.get(i-1),q);
            s.add(i, k);
        }
//        for (int i = 0; i < s.size(); i++) {
//            for (int j = 0; j < s.get(i).length; j++) {
//                System.out.print(s.get(i)[j]);
//            }
//            System.out.println();
//        }
    }

    public int[] sum (int []a, int []b){
        int []res = new int [w];
        int c=0;
        for(int i=w-1; i>=0; i--){
            if(i!=w-1){
                if((a[i+1]+b[i+1]+c)>=2) c=1;
                else c=0;
            }
            res[i]=(a[i]+b[i]+c)%2;
        }
        return res;
    }

    public int[] diff (int []a, int []b){
        int []res = new int [w];
        long a1= this.number(a);
        long b1= this.number(b);
        long c= a1-b1;
        int [] tmp = new int[w];
        if(c>=0){
            for (int j=0; j<w; j++){
                tmp[j]=(int)(c%2);
                c=(c-c%2)/2;
            }
            for (int j=0; j<w; j++) {
                res[j] = tmp[w-1-j];
            }
        }
        else {
            c=c*(-1);
            for (int j=0; j<w; j++){
                tmp[j]=(int)(c%2);
                c=(c-c%2)/2;
            }
            for (int j=0; j<w; j++) {
                res[j] = (tmp[w-1-j]+1)%2;
                tmp[w-1-j]=0;
            }
            tmp[w-1]=1;
            res=this.sum(res,tmp);
        }
        return res;
    }


    public int[] xor (int []a, int []b){
        int []res = new int [w];
        for(int i=0; i<w; i++) res[i]=(a[i]+b[i])%2;
        return res;
    }

    public int[] shift(int []value, int v){
        int []res = new int [value.length];
        v=v%value.length;
        for(int i=0; i<value.length; i++){
            res[i]=value[(i+v)%value.length];
        }
        return res;
    }
    public int[] shiftr(int []value, int v){
        int []res = new int [value.length];
        v=v%value.length;
        for(int i=0; i<value.length; i++){
            res[(i+v)%value.length]=value[i];
        }
        return res;
    }

    private int max(int a, int b) {
        if (a > b)
            return a;
        else
            return b;
    }

    private void swap(){
        int c = 8 * b / w;
        int[] A =new int[w];
        int[] B =new int[w];
        int[] summ =new int[w];
        int i = 0, j = 0;
        for (int n = 0; n < max(c, 2 * (r + 1)); n++) {
            s.set(i,this.shift(this.sum(s.get(i),this.sum(A,B)),3));
            A=s.get(i);
            summ=this.sum(A,B);

            keys.set(j,this.shift(this.sum(keys.get(j),summ),this.numberX(summ)));
            B=keys.get(j);

            i = (i + 1) % (2 * (r + 1));
            j = (j + 1) % c;
        }
    }

    public   long number(int [] value){
        long res=0;
        for(int i=0;i<value.length; i++){
            res+=(value[value.length-i-1]*Math.pow(2, i));
        }
        return res;
    }

    public   int numberX(int [] value){
        int res=0;
        for(int i=0;i<value.length; i++){
            res+=(value[value.length-i-1]*Math.pow(2, i))%value.length;
        }
        return res;
    }
}