package io.github.jevaengine.world.physics.dyn4j;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.DetectResult;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Slice;

/**
 * Graphics2D renderer for dyn4j shape types.
 * @author William Bittle
 * @version 3.1.7
 * @since 3.1.5
 */
final class Graphics2DRenderer {
    /**
     * Renders the given shape to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param shape the shape to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Shape shape, double scale, Color color) {
        // no-op
        if (shape == null) return;

        // just default the color
        if (color == null) color = Color.ORANGE;

        if (shape instanceof Circle) {
            Graphics2DRenderer.render(g, (Circle)shape, scale, color);
        } else if (shape instanceof Polygon) {
            Graphics2DRenderer.render(g, (Polygon)shape, scale, color);
        } else if (shape instanceof Segment) {
            Graphics2DRenderer.render(g, (Segment)shape, scale, color);
        } else if (shape instanceof Capsule) {
            Graphics2DRenderer.render(g, (Capsule)shape, scale, color);
        } else if (shape instanceof Ellipse) {
            Graphics2DRenderer.render(g, (Ellipse)shape, scale, color);
        } else if (shape instanceof Slice) {
            Graphics2DRenderer.render(g, (Slice)shape, scale, color);
        } else if (shape instanceof HalfEllipse) {
            Graphics2DRenderer.render(g, (HalfEllipse)shape, scale, color);
        } else {
            // unknown shape
        }
    }

    /**
     * Renders the given {@link Circle} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param circle the circle to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Circle circle, double scale, Color color) {
        double radius = circle.getRadius();
        Vector2 center = circle.getCenter();

        double radius2 = 2.0 * radius;
        Ellipse2D.Double c = new Ellipse2D.Double(
                (center.x - radius) * scale,
                (center.y - radius) * scale,
                radius2 * scale,
                radius2 * scale);

        // fill the shape
        g.setColor(color);
        g.fill(c);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(c);

        // draw a line so that rotation is visible
        Line2D.Double l = new Line2D.Double(
                center.x * scale,
                center.y * scale,
                (center.x + radius) * scale,
                center.y * scale);
        g.draw(l);
    }

    /**
     * Renders the given {@link Polygon} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param polygon the polygon to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Polygon polygon, double scale, Color color) {
        Vector2[] vertices = polygon.getVertices();
        int l = vertices.length;

        // create the awt polygon
        Path2D.Double p = new Path2D.Double();
        p.moveTo(vertices[0].x * scale, vertices[0].y * scale);
        for (int i = 1; i < l; i++) {
            p.lineTo(vertices[i].x * scale, vertices[i].y * scale);
        }
        p.closePath();

        // fill the shape
        g.setColor(color);
        g.fill(p);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(p);
    }

    /**
     * Renders the given {@link Segment} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param segment the segment to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Segment segment, double scale, Color color) {
        Vector2[] vertices = segment.getVertices();

        Line2D.Double l = new Line2D.Double(
                vertices[0].x * scale,
                vertices[0].y * scale,
                vertices[1].x * scale,
                vertices[1].y * scale);

        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(l);
    }

    /**
     * Renders the given {@link Capsule} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param capsule the capsule to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Capsule capsule, double scale, Color color) {
        // get the local rotation and translation
        double rotation = capsule.getRotation();
        Vector2 center = capsule.getCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(center.x * scale, center.y * scale);
        g.rotate(rotation);

        double width = capsule.getLength();
        double radius = capsule.getCapRadius();
        double radius2 = radius * 2.0;

        Arc2D.Double arcL = new Arc2D.Double(
                -(width * 0.5) * scale,
                -radius * scale,
                radius2 * scale,
                radius2 * scale,
                90.0,
                180.0,
                Arc2D.OPEN);
        Arc2D.Double arcR = new Arc2D.Double(
                (width * 0.5 - radius2) * scale,
                -radius * scale,
                radius2 * scale,
                radius2 * scale,
                -90.0,
                180.0,
                Arc2D.OPEN);

        // connect the shapes
        Path2D.Double path = new Path2D.Double();
        path.append(arcL, true);
        path.append(new Line2D.Double(arcL.getEndPoint(), arcR.getStartPoint()), true);
        path.append(arcR, true);
        path.append(new Line2D.Double(arcR.getEndPoint(), arcL.getStartPoint()), true);

        // set the color
        g.setColor(color);
        // fill the shape
        g.fill(path);
        // set the color
        g.setColor(getOutlineColor(color));
        // draw the shape
        g.draw(path);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link Ellipse} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param ellipse the ellipse to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Ellipse ellipse, double scale, Color color) {
        // get the local rotation and translation
        double rotation = ellipse.getRotation();
        Vector2 center = ellipse.getCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        g.translate(center.x * scale, center.y * scale);
        g.rotate(rotation);

        double width = ellipse.getWidth();
        double height = ellipse.getHeight();
        Ellipse2D.Double c = new Ellipse2D.Double(
                (-width * 0.5) * scale,
                (-height * 0.5) * scale,
                width * scale,
                height * scale);

        // fill the shape
        g.setColor(color);
        g.fill(c);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(c);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link Slice} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param slice the slice to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, Slice slice, double scale, Color color) {
        double radius = slice.getSliceRadius();
        double theta2 = slice.getTheta() * 0.5;

        // get the local rotation and translation
        double rotation = slice.getRotation();
        Vector2 circleCenter = slice.getCircleCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(circleCenter.x * scale, circleCenter.y * scale);
        g.rotate(rotation);

        // to draw the arc, java2d wants the top left x,y
        // as if you were drawing a circle
        Arc2D a = new Arc2D.Double(-radius * scale,
                -radius * scale,
                2.0 * radius * scale,
                2.0 * radius * scale,
                -Math.toDegrees(theta2),
                Math.toDegrees(2.0 * theta2),
                Arc2D.PIE);

        // fill the shape
        g.setColor(color);
        g.fill(a);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(a);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link HalfEllipse} to the given graphics context using the given scale and color.
     * @param g the graphics context
     * @param halfEllipse the halfEllipse to render
     * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color the color
     */
    public static final void render(Graphics2D g, HalfEllipse halfEllipse, double scale, Color color) {
        double width = halfEllipse.getWidth();
        double height = halfEllipse.getHeight();

        // get the local rotation and translation
        double rotation = halfEllipse.getRotation();
        Vector2 center = halfEllipse.getEllipseCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(center.x * scale, center.y * scale);
        g.rotate(rotation);

        // to draw the arc, java2d wants the top left x,y
        // as if you were drawing a circle
        Arc2D a = new Arc2D.Double(
                (-width * 0.5) * scale,
                -height * scale,
                width * scale,
                height * 2.0 * scale,
                0,
                -180.0,
                Arc2D.PIE);

        // fill the shape
        g.setColor(color);
        g.fill(a);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(a);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Returns the outline color for the given color.
     * @param color the fill color
     * @return Color
     */
    private static final Color getOutlineColor(Color color) {
        Color oc = color.darker();
        return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
    }
}

/**
 * Class used to show a simple example of using the dyn4j project using Java2D
 * for rendering.
 * <p>
 * This class can be used as a starting point for projects.
 *
 * @author Marc Risney
 * @version 3.2.0
 * @since 3.0.0
 */
public class Dyn4jDebuggerWorld extends JFrame implements KeyListener{

    private int offsetX = 0;
    private int offsetY = 0;
    private GameObject ball = null;
    private AtomicBoolean thrustOn = new AtomicBoolean(false);

    /** The serial version id */
    private static final long serialVersionUID = 5663760293144882635L;

    /** The scale 45 pixels per meter */
    public static final double SCALE = 45.0;

    /** The conversion factor from nano to base */
    public static final double NANO_TO_BASE = 1.0e9;

    private static final double GRAVITY = 980; // cm/s^2

    private Point point = null;

    public final class  CustomMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            // get the panel-space point
            point = new Point(e.getX(), e.getY());

            if(point.x > Dyn4jDebuggerWorld.this.getWidth() * .75)
                offsetX += 200;

            if(point.x < Dyn4jDebuggerWorld.this.getWidth() * .25)
                offsetX -= 200;

            if(point.y > Dyn4jDebuggerWorld.this.getHeight() * .75)
                offsetY += 200;

            if(point.y < Dyn4jDebuggerWorld.this.getHeight() * .25)
                offsetY -= 200;

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            point = null;
        }
    }

    /**
     * Custom Body class to add drawing functionality.
     *
     * @author William Bittle
     * @version 3.0.2
     * @since 3.0.0
     */
    public static class GameObject {
        /** The color of the object */
        protected Color color;

        /**
         * Default constructor.
         */
        Body body;
        public GameObject(Body body) {
            this.body = body;
            // randomly generate the color
            this.color = new Color((float) Math.random() * 0.5f + 0.5f, (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);
        }

        /**
         * Draws the body.
         * <p>
         * Only coded for polygons and circles.
         *
         * @param g
         *            the graphics object to render to
         */
        public void render(Graphics2D g) {
            // save the original transform
            AffineTransform ot = g.getTransform();


            // transform the coordinate system from world coordinates to local
            // coordinates
            AffineTransform lt = new AffineTransform();
            lt.translate(this.body.getTransform().getTranslationX() * SCALE, this.body.getTransform().getTranslationY() * SCALE);
            lt.rotate(this.body.getTransform().getRotation());

            // apply the transform
            g.transform(lt);

            // loop over all the body fixtures for this body
            for (BodyFixture fixture : this.body.getFixtures()) {
                // get the shape on the fixture
                Convex convex = fixture.getShape();
                Graphics2DRenderer.render(g, convex, SCALE, color);
            }

            // set the original transform
            g.setTransform(ot);
        }
    }

    /** The canvas to draw to */
    protected Canvas canvas;

    /** The dynamics engine */
    protected World world;

    /** Wether the example is stopped or not */
    protected boolean stopped;

    /** The time stamp for the last iteration */
    protected long last;

    /**
     * Default constructor for the window
     */
    public Dyn4jDebuggerWorld(World world) {
        super("Dyn4J Debug View");
        // setup the JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add a window listener
        this.addWindowListener(new WindowAdapter() {
            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.
             * WindowEvent)
             */
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });

        // create the size of the window
        Dimension size = new Dimension(800, 600);

        // create a canvas to paint to
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);

        // add the canvas to the JFrame
        this.add(this.canvas);

        // make the JFrame not resizable
        // (this way I dont have to worry about resize events)
        this.setResizable(false);

        MouseAdapter mouseAdapter = new CustomMouseAdapter();
        this.canvas.addMouseListener(mouseAdapter);

        // size everything
        this.pack();

        // setup the world
        this.world = world;


        // show it
        setVisible(true);

        // start it
        start();
    }

    /**
     * Start active rendering the example.
     * <p>
     * This should be called after the JFrame has been shown.
     */
    public void start() {
        // initialize the last update time
        this.last = System.nanoTime();
        // don't allow AWT to paint the canvas since we are
        this.canvas.setIgnoreRepaint(true);
        // enable double buffering (the JFrame has to be
        // visible before this can be done)
        this.canvas.createBufferStrategy(2);
    }

    /**
     * The method calling the necessary methods to update the game, graphics,
     * and poll for input.
     */
    public void gameLoop() {
        // get the graphics object to render to
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();


        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // before we render everything im going to flip the y axis and move the
        // origin to the center (instead of it being in the top left corner)
        AffineTransform yFlip = AffineTransform.getScaleInstance(0.5, 0.5);
        AffineTransform move = AffineTransform.getTranslateInstance(offsetX, offsetY);
        g.transform(yFlip);
        g.transform(move);

        // now (0, 0) is in the center of the screen with the positive x axis
        // pointing right and the positive y axis pointing up

        // render anything about the Example (will render the World objects)
        this.render(g);

        // dispose of the graphics object
        g.dispose();

        // blit/flip the buffer
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }

        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Renders the example.
     *
     * @param g
     * the graphics object to render to
     */
    protected void render(Graphics2D g) {
        // lets draw over everything with a white background
        Convex convex = Geometry.createCircle(0.1);
        Transform transform = new Transform();
        List<DetectResult> results = new ArrayList<DetectResult>();
        double x = 0;
        double y = 0;

        // convert the point from panel space to world space
        if (this.point != null) {
            x =  (this.point.getX() - 400.0) / SCALE;
            y = -(this.point.getY() - 300.0) / SCALE;
            transform.translate(x, y);
            // detect bodies under the mouse pointer (we'll radially expand it
            // so it works a little better by using a circle)
            this.world.detect(
                    convex,
                    transform,
                    null,			// no filter needed
                    false,			// include sensor fixtures
                    false,			// include inactive bodies
                    true,			// we don't need collision info
                    results);
        }
        boolean tap = false;
        double magnitude = 0.0;
        // draw all the objects in the world
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            // get the object
            GameObject go = new GameObject((Body) this.world.getBody(i));

            // render that we found any
/*            boolean changeColor = false;
            for (DetectResult r : results) {
                GameObject gor = (GameObject) r.getBody();
                if (gor == go) {
                    //results.getPenetration().getNormal().getMagnitude()

                    magnitude = r.getPenetration().getNormal().getMagnitude();

                    changeColor = true;
                    tap = true;
                    break;
                }
            }*/

            Color c = Color.red;
            // draw the object
            go.render(g);
            go.color = c;
        }

        if (this.point != null) {
            AffineTransform tx = g.getTransform();
            g.translate(x * SCALE, y * SCALE);
            Graphics2DRenderer.render(g, convex, SCALE, Color.GREEN);
            g.setTransform(tx);
            if (tap){
                //tapBall(x,y,0.1);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
    }


    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {

    }

    /**
     * Entry point for the example application.
     *
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // create the example JFrame
        //Dyn4jWorldDebugger bouncingBall2D = new Dyn4jWorldDebugger();
    }


}