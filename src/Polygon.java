import java.awt.*;

public class Polygon
{

    private Point[] points;
    private Point position;
    private double rotation;

    public Polygon(Point[] points, Point position, double rotation)
    {
        this.points = points;
        this.position = position;
        this.rotation = rotation;
    }

    public Point[] getPoints()
    {
        return points;
    }

    public Point getPosition()
    {
        return position;
    }

    public void setPosition(Point position)
    {
        this.position = position;
    }

    public double getRotation()
    {
        return rotation;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public Point[] getTransformedPoints()
    {
        Point[] transformed = new Point[points.length];
        double rad = Math.toRadians(rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        for (int i = 0; i < points.length; i++)
        {
            double rx = points[i].x * cos - points[i].y * sin;
            double ry = points[i].x * sin + points[i].y * cos;
            transformed[i] = new Point(rx + position.x, ry + position.y);
        }
        return transformed;
    }

    public boolean collides(Polygon other)
    {
        Point[] tp1 = this.getTransformedPoints();
        Point[] tp2 = other.getTransformedPoints();
        double[] bb1 = getBoundingBox(tp1);
        double[] bb2 = getBoundingBox(tp2);
        return bb1[0] < bb2[2] && bb1[2] > bb2[0] && bb1[1] < bb2[3] && bb1[3] > bb2[1];
    }

    public boolean contains(Point p)
    {
        Point[] tp = getTransformedPoints();
        int n = tp.length;
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++)
        {
            if ((tp[i].y > p.y) != (tp[j].y > p.y) &&
                p.x < (tp[j].x - tp[i].x) * (p.y - tp[i].y) / (tp[j].y - tp[i].y) + tp[i].x)
                {
                inside = !inside;
            }
        }
        return inside;
    }

    public void paint(Graphics brush)
    {
        Point[] tp = getTransformedPoints();
        int[] xPoints = new int[tp.length];
        int[] yPoints = new int[tp.length];
        for (int i = 0; i < tp.length; i++)
        {
            xPoints[i] = tp[i].getX();
            yPoints[i] = tp[i].getY();
        }
        brush.fillPolygon(xPoints, yPoints, tp.length);
    }

    private double[] getBoundingBox(Point[] pts)
    {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Point p : pts)
        {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }
        return new double[]{minX, minY, maxX, maxY};
    }
}
