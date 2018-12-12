package intellij;

import frames.core.Graph;
import frames.processing.Scene;
import frames.processing.Shape;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;
import processing.opengl.PShader;

public class ShadowMap extends PApplet {
  Graph.Type shadowMapType = Graph.Type.ORTHOGRAPHIC;
  Scene scene;
  Shape[] shapes;
  PGraphics shadowMap;
  PShader depthShader;
  float zNear = 50;
  float zFar = 1000;
  int w = 1000;
  int h = 1000;

  public void settings() {
    size(w, h, P3D);
  }

  public void setup() {
    scene = new Scene(this);
    scene.setRadius(max(w, h));
    shapes = new Shape[20];
    for (int i = 0; i < shapes.length; i++) {
      shapes[i] = new Shape(scene) {
        @Override
        public void setGraphics(PGraphics pg) {
          pg.pushStyle();
          if (scene.trackedFrame("light") == this) {
            Scene.drawAxes(pg, 150);
            pg.fill(scene.isTrackedFrame(this) ? 255 : 25, 100, 255);
            Scene.drawEye(pg, shadowMap, shadowMapType, this, zNear, zFar);

          } else {
            if (pg == shadowMap)
              pg.noStroke();
            else {
              pg.strokeWeight(3);
              pg.stroke(0, 255, 255);
            }
            pg.fill(255, 0, 0);
            pg.box(80);
          }
          pg.popStyle();
        }
      };
      shapes[i].randomize();
      shapes[i].setHighlighting(Shape.Highlighting.NONE);
    }
    scene.setRadius(scene.radius() * 1.2f);
    scene.fit(1);

    depthShader = loadShader("/home/pierre/IdeaProjects/frames/testing/data/depth/depth.glsl");
    depthShader.set("near", zNear);
    depthShader.set("far", zFar);
    shadowMap = createGraphics(w / 2, h / 2, P3D);
    shadowMap.shader(depthShader);

    scene.setTrackedFrame("light", shapes[(int) random(0, shapes.length - 1)]);
  }

  public void draw() {
    background(90, 80, 125);
    // 1. Fill in and display front-buffer
    scene.traverse();
    // 2. Fill in shadow map using the light point of view
    if (scene.trackedFrame("light") != null) {
      shadowMap.beginDraw();
      shadowMap.background(120);
      scene.traverse(shadowMap, shadowMapType, scene.trackedFrame("light"), zNear, zFar);
      shadowMap.endDraw();
      // 3. Display shadow map
      scene.beginHUD();
      image(shadowMap, w / 2, h / 2);
      scene.endHUD();
    }
  }

  public void mouseMoved(MouseEvent event) {
    if (event.isControlDown())
      scene.cast("light");
    else
      scene.cast();
  }

  public void mouseDragged() {
    if (mouseButton == LEFT)
      scene.spin();
    else if (mouseButton == RIGHT)
      scene.translate();
    else
      scene.moveForward(mouseX - pmouseX);
  }

  public void mouseWheel(MouseEvent event) {
    scene.scale(event.getCount() * 20);
  }

  public void keyPressed() {
    if (key == 'f')
      scene.fitFOV(1);
    if (key == 'a')
      scene.fitFOV();
    if (key == '1')
      scene.setFOV(1);
    if (key == '3')
      scene.setFOV(PI / 3);
    if (key == '4')
      scene.setFOV(PI / 4);
    if (key == 'o')
      if (shadowMapType == Graph.Type.ORTHOGRAPHIC)
        shadowMapType = Graph.Type.PERSPECTIVE;
      else
        shadowMapType = Graph.Type.ORTHOGRAPHIC;
    if (key == 't')
      scene.togglePerspective();
    if (key == 'p')
      scene.eye().position().print();
  }

  public static void main(String args[]) {
    PApplet.main(new String[]{"intellij.ShadowMap"});
  }
}
