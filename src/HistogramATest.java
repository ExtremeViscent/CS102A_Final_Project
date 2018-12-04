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

   private static Formats getFormatsFrom( JsonObject obj) {  // TODO for default values
      Formats fmts = new Formats();
      fmts.margins = toDoubleArray( obj.getJsonArray( "margins"));
      fmts.isBarFilled = obj.getBoolean( "isbarfilled");
      fmts.barFillColor = getColorFrom( obj.getJsonArray( "barfillcolor"));
      fmts.hasBarFrame = obj.getBoolean( "hasbarframe");
      fmts.barFrameColor = getColorFrom( obj.getJsonArray( "barframecolor"));
      fmts.hasBorder = obj.getBoolean( "hasborder");
      fmts.borderColor = getColorFrom( obj.getJsonArray( "bordercolor"));
      fmts.rulerColor = getColorFrom( obj.getJsonArray( "rulercolor"));
      fmts.rulerMarkColor = getColorFrom( obj.getJsonArray( "rulermarkcolor"));
      fmts.hasRightRuler = obj.getBoolean( "isbarfilled");
      fmts.keyColor = getColorFrom( obj.getJsonArray( "keycolor"));
      fmts.hasHeader = obj.getBoolean( "hasheader");
      fmts.headerColor = getColorFrom( obj.getJsonArray( "headercolor"));
      fmts.hasFooter = obj.getBoolean( "hasfooter");
      fmts.footerColor = getColorFrom( obj.getJsonArray( "footercolor"));
      return fmts;
   }

   private static HistogramData getDataFrom( JsonObject obj) {
      HistogramData data = new HistogramData();
      data.header = obj.getString( "header", "");
      data.footer = obj.getString( "footer", "");
      data.minValue = obj.getJsonNumber( "minvalue").doubleValue(); // TODO for default value
      data.keys = toStringArray( obj.getJsonArray( "keys"));
      data.values = toDoubleArray( obj.getJsonArray( "values"));
      return data;
   }
}
