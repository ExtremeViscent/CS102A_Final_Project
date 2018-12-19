import java.awt.*;
import java.time.chrono.MinguoChronology;
import java.util.Arrays;
import java.util.logging.LogManager;



public class HistogramB {
    Canvas c;
    Formats f;
    HistogramData d;
    int[][] color;
    double[] xValue;  // MIN, MAX
    double[] yValue;  // MIN, MAX
    double[] xScale;  // MIN, MAX
    double[] yScale;  // MIN, MAX
    int rulerGrade;
    double rulerStep;


    public HistogramB(Canvas c, Formats f, HistogramData d) {
        this.c = c;
        this.f = f;
        this.d = d;
        xValue = new double[2];
        yValue = new double[2];
        xScale = new double[2];
        yScale = new double[2];
        setHistogramParameters();
    }

    private void setHistogramParameters () {
        double[] a = d.data[0].values;
        xValue[MIN] = -1;
        xValue[MAX] = a.length;

        yValue[MIN] = d.minValue;
        double max = a[0];
        for (int i = 0; i < d.objectsCount; i++) {
            a=d.data[i].values;
            for (int r = 1; r < a.length; r++)
                if (max < a[r]) max = a[r];
        }
        double span = max - yValue[MIN];
        double factor = 1.0;
        if (span >= 1)
            while (span >= 10) { span /= 10; factor *= 10; }
        else
            while (span < 1)   { span *= 10; factor /= 10; }
        int nSpan = (int)Math.ceil(span);
        yValue[MAX] = yValue[MIN] + (factor) * nSpan;
        switch (nSpan) {
            case 1 :  rulerGrade = 5; rulerStep = factor/5; break;
            case 2 :
            case 3 :  rulerGrade = nSpan*2; rulerStep = factor/2; break;
            default : rulerGrade = nSpan; rulerStep = factor; break;
        }
    }

    public void draw () {
        StdDraw.enableDoubleBuffering();
        setCanvas();
        plotBars();
        plotRuler();
        plotKeys();
        plotIcon();
        plotShoes();
        plotFangKuai();
        if (f.hasBorder) plotBorder();
        if (f.hasRightRuler) plotRightRuler();
        if (f.hasHeader) plotHeader();
        if (f.hasFooter) plotFooter();
        StdDraw.show();
    }

    private void setCanvas () {
        int m = d.objectsCount;
        color = new int[m][3];
        for(int i = 0; i < m; i++){//给每一个object赋颜色!!!!!!!!!!!
            int a1 = (int)(Math.random()*255);
            int a2 = (int)(Math.random()*255);
            int a3 = (int)(Math.random()*255);
            color[i][0] = a1;
            color[i][1] = a2;
            color[i][2] = a3;
        }
        StdDraw.setCanvasSize( c.x, c.y );
        setOriginalScale();
        StdDraw.clear( c.bgColor);
        StdDraw.setPenColor( c.color);
    }

    private void setHistogramScale (int nBars) {
        double span = yValue[MAX] - yValue[MIN] + 1;
        double ySpacing = span / (1 - f.margins[NORTH] - f.margins[SOUTH]);
        yScale[MIN] = yValue[MIN] - f.margins[SOUTH] * ySpacing - 1;
        yScale[MAX] = yValue[MAX] + f.margins[NORTH] * ySpacing;
        StdDraw.setYscale( yScale[MIN], yScale[MAX]);
        double xSpacing = (nBars+1) / (1 - f.margins[WEST] - f.margins[EAST]);
        xScale[MIN] = - f.margins[WEST] * xSpacing - 1;
        xScale[MAX] = nBars + f.margins[EAST] * xSpacing;
        StdDraw.setXscale( xScale[MIN], xScale[MAX]);
        //rintln(xScale[0]+" "+xScale[1]);
    };

    private void setOriginalScale() {
        StdDraw.setXscale( c.xScale[MIN], c.xScale[MAX]);
        StdDraw.setYscale( c.yScale[MIN], c.yScale[MAX]);
    }

    private void plotBars() {
        if (f.form.equals("group")) {
            for (int j = 0; j < d.objectsCount; j++) {//j组类型数据
                double offset = -0.25 + j * 0.5 / d.objectsCount;
                double[] a = d.data[j].values;
                int n = a.length;
                setHistogramScale(n);
                if (f.isBarFilled) {
                    StdDraw.setPenColor(f.barFillColor);
                    for (int i = 0; i < n; i++) {
                        StdDraw.filledRectangle(i + offset, a[i] / 2, 0.25 / d.objectsCount, a[i] / 2);
                        // (x, y, halfWidth, halfHeight)
                    }
                }
                if (f.hasBarFrame) {
                    StdDraw.setPenColor(f.barFrameColor);
                    for (int i = 0; i < n; i++) {
                        StdDraw.rectangle(i + offset, a[i] / 2, 0.25 / d.objectsCount, a[i] / 2);
                        // (x, y, halfWidth, halfHeight)
                    }
                }
            }
        }
        else {
            double[] b = d.data[1].values;
            int n = b.length;
            int m = d.objectsCount;
            double[][] a = new double[m][n];

            for (int j = 0; j < m; j++) {//一个object&给二维数组a赋值
                double[] temp = d.data[j].values;//进行转移赋值的
                for (int i = 0; i < n; i++) {
                    a[j][i] = temp[i];
                }
            }//给二维数组a赋值


            for (int i = 0; i < n; i++) {//每行
                double[] bijiao = new double[m];
                for (int j = 0; j < m; j++) {//每列的比较
                    bijiao[j] = a[j][i];
                }
                int[] address = new int[m];
                for(int k = 0;k < m;k++) {
                    address[k] = k;
                }
                for(int j = 0; j<m ;j++ ) {//
                    for(int l = 0 ; l<m-1 ; l++) {
                        if(bijiao[l] > bijiao[l+1]) {
                            double min = bijiao[l+1];
                            bijiao[l+1] = bijiao[l];
                            bijiao[l] = min;

                            int f = address[l+1];
                            address[l+1] = address[l];
                            address[l] = f;
                        }
                    }
                }
                for (int j = m; j > 0; j--) {//每列的打印
                    int index = 0;
                    setHistogramScale(n);
                    if (f.isBarFilled) {
                        StdDraw.setPenColor(color[address[j-1]][0],color[address[j-1]][1],color[address[j-1]][2]);
                        StdDraw.filledRectangle(i, bijiao[j-1] / 2, 0.25, bijiao[j-1] / 2);
                        // (x, y, halfWidth, halfHeight)

                    }
                    if (f.hasBarFrame) {

                        StdDraw.setPenColor(color[address[j-1]][0],color[address[j-1]][1],color[address[j-1]][2]);
                        StdDraw.rectangle(i, bijiao[j-1] / 2, 0.25, bijiao[j-1] / 2);
                        // (x, y, halfWidth, halfHeight)
                    }
                }
            }
        }
    }

    private void plotRuler() {
        Font font = new Font( "consolas", Font.PLAIN, 12 ); // TO BE Customized
        StdDraw.setFont( font );
        StdDraw.setPenColor( f.rulerColor );
        final double x0 = xValue[MIN] - 0.05, x1 = xValue[MIN] + 0.05;
        String[] mark = new String[rulerGrade+1];
        for (int i = 0; i <= rulerGrade; i++) {
            double y = yValue[MIN] + i * rulerStep;
            mark[i] = numberForRuler( y );
            StdDraw.line( x0, y, x1, y );
        }
        int len = maxMarkLength( mark );
        final double xs = xScale[MIN] + 0.7 * (xValue[MIN] - xScale[MIN]);
        for (int i = 0; i <= rulerGrade; i++) {
            double y = yValue[MIN] + i * rulerStep;
            StdDraw.text( xs, y, String.format( "%" + len + "s", mark[i] ));
        }
    }

    private String numberForRuler (double x) {   // TO BE Customized
        if (yValue[MAX] >= 5 && rulerStep > 1) return "" + (int)x;
        if (rulerStep > 0.1) return String.format( "%.1f", x );
        if (rulerStep > 0.01) return String.format( "%.2f", x );
        if (rulerStep > 0.001) return String.format( "%.3f", x );
        if (rulerStep > 0.0001) return String.format( "%.4f", x );
        if (rulerStep > 0.00001) return String.format( "%.5f", x );
        return String.format( "%g", x );
    }

    private int maxMarkLength (String[] sa) {
        int n = sa[0].length();
        for (String s : sa)
            if (n < s.length()) n = s.length();
        return n;
    }

    private void plotKeys() {
        Font font = new Font( "consolas", Font.PLAIN, 12 ); // TO BE Customized
        StdDraw.setFont( font );
        StdDraw.setPenColor( f.keyColor );
        final double y = yValue[MIN] - (yValue[MIN]-yScale[MIN])*0.2;
        for (int i = 0; i < d.data[0].keys.length; i++) {
            if (d.data[0].keys[i].length() >= 1) {
                double x = xValue[MIN] + 1 + i;
                StdDraw.text( x, y, d.data[0].keys[i]);
            }
        }
    }

    private void plotBorder() {
        double x = .5 * (xValue[MIN] + xValue[MAX]);
        double y = .5 * (yValue[MIN] + yValue[MAX]);
        double halfWidth  = .5 * (xValue[MAX] - xValue[MIN]);

        //double ineed=(.5 * (yValue[MIN] + yValue[MAX]))+(.5 * (yValue[MAX] - yValue[MIN]))
        double halfHeight = .5 * (yValue[MAX] - yValue[MIN]);
        //rintln(halfHeight+"+"+halfWidth+"x"+x+"y"+y+" "+yScale[MAX]);
        StdDraw.setPenColor( f.borderColor );
        StdDraw.rectangle( x, y, halfWidth, halfHeight);
    }

    private void plotRightRuler() {
        Font font = new Font( "consolas", Font.PLAIN, 12 ); // TO BE Customized
        StdDraw.setFont( font );
        StdDraw.setPenColor( f.rulerColor );
        final double x0 = xValue[MAX] - 0.05, x1 = xValue[MAX] + 0.05;
        String[] mark = new String[rulerGrade+1];
        for (int i = 0; i <= rulerGrade; i++) {
            double y = yValue[MIN] + i * rulerStep;
            mark[i] = numberForRuler( y );
            StdDraw.line( x0, y, x1, y );
        }
        int len = maxMarkLength( mark );
        final double xs = xScale[MAX] - 0.25 * (xValue[MIN] - xScale[MIN]);
        for (int i = 0; i <= rulerGrade; i++) {
            double y = yValue[MIN] + i * rulerStep;
            StdDraw.text( xs, y, String.format( "%-" + len + "s", mark[i] ));
        }
    }

    private void plotHeader() {
        Font font = new Font( "calibri", Font.PLAIN, 20 ); // TO BE Customized
        StdDraw.setFont( font );
        double x = .5 * (xScale[MIN] + xScale[MAX]);
        double y = .5 * (yValue[MAX] + yScale[MAX]);
        StdDraw.setPenColor( f.headerColor );
        StdDraw.text( x, y, d.header );
    }

    private void plotFooter() {
        Font font = new Font( "Arial", Font.BOLD, 16 ); // TO BE Customized
        StdDraw.setFont( font );
        double x = .5 * (xScale[MIN] + xScale[MAX]);
        double y = (yValue[MIN]-(yValue[MIN]-yScale[MIN])/3*2.15);
        StdDraw.setPenColor( f.footerColor );
        StdDraw.text( x, y, d.footer );
    }

    private void plotIcon(){
        double ineed=(.5 * (yValue[MIN] + yValue[MAX]))+(.5 * (yValue[MAX] - yValue[MIN]));
        double scaledHeight=(yScale[MAX]-ineed);
        double scaledWidth=scaledHeight/(yScale[1]-yScale[0])*(130.0/62.0)*(xScale[1]-xScale[0]);
        System.out.println(ineed+" "+scaledHeight+" "+scaledWidth);
        scaledHeight*=0.8;
        scaledWidth*=0.8;
        StdDraw.picture((xScale[MAX])-scaledWidth/2,(yScale[MAX])-scaledHeight/2,"LOGO.png",scaledWidth,scaledHeight);
    }

    private void plotShoes(){
        double xlength = xScale[MAX] - xScale[MIN];
        double ylength = yScale[MAX] - yScale[MIN];
        double x0 = (xScale[MAX] + xScale[MIN])/2;
        double y0 = (yScale[MAX] + yScale[MIN])/2;
        double changdu1 = (xlength)/4;
        double changdu2 = 3*changdu1;
        double kuandu = 0.2*(yValue[MIN]-yScale[MIN]);
        double a1 = changdu1/2-xlength/2;
        double a2 = xlength/2-changdu2/2;
        double b = kuandu/2-ylength/2;
        StdDraw.setPenColor(43,183,179);
        StdDraw.filledRectangle(x0+a1, y0+kuandu/2-ylength/2, changdu1/2 , 0.1*(yValue[MIN]-yScale[MIN]));
        StdDraw.setPenColor(237,108,0);
        StdDraw.filledRectangle(x0+a2, y0+kuandu/2-ylength/2, changdu2/2 , 0.1*(yValue[MIN]-yScale[MIN]));
    }

    private void plotFangKuai(){
        double xlength = xScale[MAX] - xScale[MIN];
        double ylength = yScale[MAX] - yScale[MIN];
        double xzuobiaolength = xValue[MAX] - xScale[MIN];
        double x0 = (xScale[MAX] + xScale[MIN])/2;
        double y0 = (yScale[MAX] + yScale[MIN])/2;
        double yjianju = yValue[MIN]-yScale[MIN];
        double xjianju = xlength*yjianju/ylength;
        double ychangdu = yjianju*0.1;
        double xchangdu = xjianju*0.045;
        /*StdDraw.setPenColor(0,0,255);
        StdDraw.filledRectangle(x0,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.33),xchangdu/2,ychangdu/2);
        StdDraw.setPenColor(0,0,255);
        StdDraw.filledRectangle(x0,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.33),xchangdu/2,ychangdu/2);
        StdDraw.setPenColor(0,255,0);
        StdDraw.filledRectangle(x0,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.52),xchangdu/2,ychangdu/2);
        StdDraw.setPenColor(0,255,0);
        StdDraw.filledRectangle(x0,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.52),xchangdu/2,ychangdu/2);*/
        double jiangekuandu = xzuobiaolength / ((d.objectsCount+1)/2);
        for(int i = 0; i < d.objectsCount/2; i++){//打印方块！！！！！！！！！！！！！！！！！！！！！
            StdDraw.setPenColor(color[i][0],color[i][1],color[i][2]);
            StdDraw.filledRectangle(-jiangekuandu+jiangekuandu*i,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.52),xchangdu/2,ychangdu/2);
            StdDraw.setPenColor(color[i][0],color[i][1],color[i][2]);
            StdDraw.filledRectangle(-jiangekuandu+jiangekuandu*i,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.52),xchangdu/2,ychangdu/2);
        }
        for(int i = d.objectsCount/2; i < d.objectsCount; i++){
            StdDraw.setPenColor(color[i][0],color[i][1],color[i][2]);
            StdDraw.filledRectangle(0 + -jiangekuandu+jiangekuandu*(i-d.objectsCount/2),yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.33),xchangdu/2,ychangdu/2);
            StdDraw.setPenColor(color[i][0],color[i][1],color[i][2]);
            StdDraw.filledRectangle(0 + -jiangekuandu+jiangekuandu*(i-d.objectsCount/2),yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.33),xchangdu/2,ychangdu/2);
        }


        Font font = new Font( "Arial", Font.BOLD, 8 ); // TO BE Customized
        StdDraw.setFont( font );
        for(int i = 0; i < d.objectsCount/2; i++){//打印字！！！！！！！！！！！！！！！！！！！！！！！
            StdDraw.text(-jiangekuandu+jiangekuandu*i+jiangekuandu*0.5,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.52),d.data[i].name);
        }
        for(int i = d.objectsCount/2; i < d.objectsCount; i++){
            StdDraw.text(-jiangekuandu+jiangekuandu*(i-d.objectsCount/2)+jiangekuandu*0.5,yValue[MIN]-(yValue[MIN]-yScale[MIN]*0.33),d.data[i].name);
        }


    }

    private final static int NORTH = 0;
    private final static int SOUTH = 1;
    private final static int WEST  = 2;
    private final static int EAST  = 3;
    private final static int MIN  = 0;
    private final static int MAX  = 1;
}
