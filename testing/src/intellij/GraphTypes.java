package intellij;

import frames.core.Frame;
import frames.core.Graph;
import frames.primitives.Quaternion;
import frames.primitives.Vector;
import frames.processing.Scene;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class GraphTypes extends PApplet {
  StdCamera scene;
  Scene auxScene, focus;
  Frame boxFrame;
  boolean box, sphere;

  int w = 1200;
  int h = 1200;

  public void settings() {
    size(w, h, P3D);
  }

  public void setup() {
    scene = new StdCamera(this, w, h / 2);
    //scene1.setZClippingCoefficient(1);
    scene.setRadius(200);
    scene.setType(Graph.Type.ORTHOGRAPHIC);
    //scene1.fitBallInterpolation();
    scene.fitBall();

    // enable computation of the frustum planes equations (disabled by default)
    scene.enableBoundaryEquations();

    // Note that we pass the upper left corner coordinates where the scene1
    // is to be drawn (see drawing code below) to its constructor.
    auxScene = new Scene(this, w, h / 2, 0, h / 2);
    //scene2.setType(Graph.Type.ORTHOGRAPHIC);
    auxScene.setRadius(400);
    //scene2.fitBallInterpolation();
    auxScene.fitBall();
    boxFrame = new Frame(auxScene);
    boxFrame.rotate(new Quaternion(new Vector(0, 1, 0), QUARTER_PI));
  }

  public void keyPressed() {
    if (key == ' ')
      scene.toggleMode();
    if (key == 'f')
      scene.fitBall();
    if (key == 'g')
      scene.autoAperture();
    if (key == 'b')
      box = !box;
    if (key == 's')
      sphere = !sphere;
    if (key == 'a') {
      Vector zNear = new Vector(0, 0, scene.zNear());
      Vector zFar = new Vector(0, 0, scene.zFar());
      Vector zNear2ZFar = Vector.subtract(zFar, zNear);
      Vector zNear2ZFarEye = scene.eye().displacement(zNear2ZFar);
      println("zNear2ZFar: " + zNear2ZFar.magnitude());
      println("zNear2ZFarEye: " + zNear2ZFarEye.magnitude());
      println("2*radius*sqrt(3): " + 2 * scene.radius() * sqrt(3));
      println(version1() + " " + version2() + " eye magnitude: " + scene.eye().magnitude());
      println((scene.type() == Graph.Type.ORTHOGRAPHIC ? "ORTHO" : "PERSP") + " zNear: " + scene.zNear() + " zFar: " + scene.zFar());
      scene.eye().position().print();
    }
    if (key == 'n')
      scene.setAperture(1);
    if (key == 'm')
      scene.setAperture(PI / 3);
    if (key == 't') {
      if (scene.type() == Graph.Type.PERSPECTIVE) {
        scene.setType(Graph.Type.ORTHOGRAPHIC);
      } else {
        scene.setType(Graph.Type.PERSPECTIVE);
      }
      //scene1.fitBallInterpolation();
      //scene1.fitBall();
    }
    if (key == 'e')
      if (auxScene.trackedFrame() == boxFrame)
        auxScene.resetTrackedFrame();
      else
        auxScene.setTrackedFrame(boxFrame);
    if (key == '+')
      scene.eye().rotate(0, 1, 0, QUARTER_PI / 2);
    if (key == '-')
      scene.eye().rotate(0, 1, 0, -QUARTER_PI / 2);
  }

  public String version1() {
    float z = Vector.scalarProjection(Vector.subtract(scene.eye().position(), scene.center()), scene.eye().zAxis()) - scene.zClippingCoefficient() * scene.radius();
    // Prevents negative or null zNear values.
    float zMin = scene.zNearCoefficient() * scene.zClippingCoefficient() * scene.radius();
    return ("frames z: " + z + " frames zMin: " + zMin);
  }

  public String version2() {
    /*
    float zNearScene = zClippingCoefficient() * sceneRadius();
    float z = distanceToSceneCenter() - zNearScene;
    // Prevents negative or null zNear values.
    float zMin = zNearCoefficient() * zNearScene;
    */

    float zNearScene = scene.zClippingCoefficient() * scene.radius();
    float z = distanceToSceneCenter() - zNearScene;
    float zMin = scene.zNearCoefficient() * zNearScene;
    return ("Viewer z: " + z + " Viewer zMin: " + zMin);
  }

  float distanceToSceneCenter() {
    return Math.abs((scene.eye().location(scene.center())).z());
  }

  public void mouseDragged() {
    if (mouseButton == LEFT)
      focus.spin();
    else if (mouseButton == RIGHT)
      focus.translate();
    else
      focus.moveForward(mouseX - pmouseX);
  }

  public void mouseWheel(MouseEvent event) {
    focus.scale(event.getCount() * 20);
    //focus.zoom(event.getCount() * 50);
  }

  public void mouseClicked(MouseEvent event) {
    if (event.getCount() == 2)
      if (event.getButton() == LEFT)
        focus.focus();
      else
        focus.align();
  }

  void draw(PGraphics graphics) {
    graphics.background(0);
    graphics.noStroke();
    // the main viewer camera is used to cull the sphere object against its frustum
    switch (scene.ballVisibility(new Vector(0, 0, 0), scene.radius() * 0.6f)) {
      case VISIBLE:
        graphics.fill(0, 255, 0);
        graphics.sphere(scene.radius() * 0.6f);
        break;
      case SEMIVISIBLE:
        graphics.fill(255, 0, 0);
        graphics.sphere(scene.radius() * 0.6f);
        break;
      case INVISIBLE:
        break;
    }
  }

  void handleMouse() {
    focus = mouseY < h / 2 ? scene : auxScene;
  }

  public void draw() {
    handleMouse();
    scene.beginDraw();
    scene.frontBuffer().background(0);
    //draw(canvas1);
    scene.drawAxes();

    scene.endDraw();
    scene.display();

    auxScene.beginDraw();
    auxScene.frontBuffer().background(0);
    //draw(canvas2);
    auxScene.drawAxes();

    if (sphere) {
      auxScene.frontBuffer().pushStyle();
      auxScene.frontBuffer().fill(255, 0, 255, 160);
      auxScene.frontBuffer().sphere(scene.radius());
      auxScene.frontBuffer().popStyle();
    }

    if (box) {
      auxScene.frontBuffer().pushStyle();
      auxScene.frontBuffer().pushMatrix();
      auxScene.applyTransformation(boxFrame);
      auxScene.frontBuffer().fill(0, 255, 0, 160);
      auxScene.frontBuffer().box(2 * scene.radius());
      auxScene.frontBuffer().popMatrix();
      auxScene.frontBuffer().popStyle();
    }

    // draw with axes
    //eye
    auxScene.frontBuffer().pushStyle();
    auxScene.frontBuffer().stroke(255, 255, 0);
    auxScene.frontBuffer().fill(255, 255, 0, 160);
    auxScene.drawEye(scene);
    auxScene.frontBuffer().popStyle();
    //axes
    auxScene.frontBuffer().pushMatrix();
    auxScene.applyTransformation(scene.eye());
    auxScene.drawAxes(60);
    auxScene.frontBuffer().popMatrix();

    auxScene.endDraw();
    auxScene.display();
  }

  public static void main(String args[]) {
    PApplet.main(new String[]{"intellij.GraphTypes"});
  }

  public class StdCamera extends Scene {
    boolean standard;

    public StdCamera(PApplet applet, int w, int h) {
      super(applet, w, h);
      standard = false;
    }

    public void toggleMode() {
      standard = !standard;
    }

    public boolean isStandard() {
      return standard;
    }

    @Override
    public float zNear() {
      if (standard)
        return 0.001f;
      else
        return super.zNear();
    }

    @Override
    public float zFar() {
      if (standard)
        return 1000.0f;
      else
        return super.zFar();
    }
  }
}
