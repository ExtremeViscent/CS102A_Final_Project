import java.awt.Color;
import java.io.*;
import javax.json.*;

public class HistogramATest {
   public static void main(String[] args) {
      HistogramA h = createHistogramAFrom( args[0] );
      h.draw();
   }   

   private static HistogramA createHistogramAFrom (String fileName) {
      HistogramA h = null;
      try (
         InputStream is = new FileInputStream( new File( fileName ));
         JsonReader rdr = Json.createReader(is)
      ) {
         JsonObject obj = rdr.readObject().getJsonObject( "histograma" );
         Canvas canvas = getCanvasFrom( obj.getJsonObject( "canvas" ));
         Formats fmts = getFormatsFrom( obj.getJsonObject( "formats" ));
         HistogramData data = getDataFrom( obj.getJsonObject( "data" ));
         h =  new HistogramA( canvas, fmts, data);
      } catch (IOException e) {
         System.out.println( e.getMessage());
      };
      return h;
   }     
      
   private static Canvas getCanvasFrom( JsonObject obj) {
      Canvas canvas = new Canvas();

      JsonArray szArray = obj.getJsonArray( "size" );
      if (szArray != null ) {  // otherwise, use the default size
         int[] size = toIntArray( szArray);
         canvas.x = size[0]; 
         canvas.y = size[1];
      } 

      JsonArray xsArray = obj.getJsonArray( "xscale" );
      if (xsArray != null )  // otherwise, use the default xScale
         canvas.xScale = toDoubleArray( xsArray );

      JsonArray ysArray = obj.getJsonArray( "yscale" );
      if (ysArray != null )  // otherwise, use the default yScale
         canvas.yScale = toDoubleArray( ysArray );

      JsonArray bgcArray = obj.getJsonArray( "bgcolor");
      if (bgcArray != null )  // otherwise, use the default bgColor
         canvas.bgColor = getColorFrom( bgcArray );

      JsonArray cArray = obj.getJsonArray( "color");
      if (cArray != null )    // otherwise, use the default color
         canvas.color = getColorFrom( cArray );

      return canvas;
   }

   private static int[] toIntArray (JsonArray jsa) {
      int[] a = new int[jsa.size()];
      for (int i = 0; i < jsa.size(); i++)
         a[i] = jsa.getInt(i); 
      return a;
   }

   private static double[] toDoubleArray (JsonArray jsa) {
      double[] a = new double[jsa.size()]; 
      for (int i = 0; i < jsa.size(); i++)
         a[i] = jsa.getJsonNumber(i).doubleValue(); 
      return a;
   }
 
   private static String[] toStringArray (JsonArray jsa) {
      String[] s = new String[jsa.size()]; 
      for (int i = 0; i < jsa.size(); i++)
         s[i] = jsa.getString(i); 
      return s;
   }

   private static Color getColorFrom (JsonArray jsa) {
      int[] c = toIntArray( jsa);
      return new Color( c[0], c[1], c[2]);
   }

   private static Formats getFormatsFrom( JsonObject obj) {
      Formats fmts = new Formats();
      try{fmts.isBarFilled = obj.getBoolean( "isbarfilled");}catch(Exception ignored){}
      try{fmts.barFillColor = getColorFrom( obj.getJsonArray( "barfillcolor"));}catch(Exception ignored){}
      try{fmts.hasBarFrame = obj.getBoolean( "hasbarframe");}catch(Exception ignored){}
      try{fmts.barFrameColor = getColorFrom( obj.getJsonArray( "barframecolor"));}catch(Exception ignored){}
      try{fmts.hasBorder = obj.getBoolean( "hasborder");}catch(Exception ignored){}
      try{fmts.borderColor = getColorFrom( obj.getJsonArray( "bordercolor"));}catch(Exception ignored){}
      try{fmts.rulerColor = getColorFrom( obj.getJsonArray( "rulercolor"));}catch(Exception ignored){}
      try{fmts.rulerMarkColor = getColorFrom( obj.getJsonArray( "rulermarkcolor"));}catch(Exception ignored){}
      try{fmts.hasRightRuler = obj.getBoolean( "isbarfilled");}catch(Exception ignored){}
      try{fmts.keyColor = getColorFrom( obj.getJsonArray( "keycolor"));}catch(Exception ignored){}
      try{fmts.hasHeader = obj.getBoolean( "hasheader");}catch(Exception ignored){}
      try{fmts.headerColor = getColorFrom( obj.getJsonArray( "headercolor"));}catch(Exception ignored){}
      try{fmts.hasFooter = obj.getBoolean( "hasfooter");}catch(Exception ignored){}
      try{fmts.footerColor = getColorFrom( obj.getJsonArray( "footercolor"));}catch(Exception ignored){}
      return fmts;
   }

   private static HistogramData getDataFrom( JsonObject obj) {
      HistogramData data = new HistogramData();
      data.header = obj.getString( "header", "");
      data.footer = obj.getString( "footer", "");
      try{  data.minValue = obj.getJsonNumber( "minvalue").doubleValue(); }catch(Exception ignored){}
      if (obj.containsKey("objectCount")){
         data.objectsCount = obj.getInt("objectCount");
         data.data=new SingleObjectData[data.objectsCount];
         for (int i = 0; i < data.objectsCount; i++) {
            data.data[i]=new SingleObjectData();
            data.data[i].name=toStringArray(obj.getJsonArray("name"))[i];
         }
         for (int i=0;i<data.objectsCount;i++) {
            JsonObject obj_= obj.getJsonObject(data.data[i].name);
            data.data[i].keys = toStringArray( obj.getJsonArray( "keys"));
            data.data[i].values = toDoubleArray( obj.getJsonArray( "values"));
         }
      }
      else {
         data.objectsCount=1;
         data.data=new SingleObjectData[1];
         data.data[0]=new SingleObjectData();
         data.data[0].keys = toStringArray( obj.getJsonArray( "keys"));
         data.data[0].values = toDoubleArray( obj.getJsonArray( "values"));}
      return data;
   }
}
