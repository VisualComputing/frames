// adapted from: https://learnopengl.com/Advanced-OpenGL/Depth-testing

uniform float near;
uniform float far;

// expect values in the range [0..1]
float linearizeDepth(float depth) {
  // back to NDC
  float z = depth * 2.0 - 1.0;
  // depth values are returned in the range [near..far]
  return (2.0 * near * far) / (far + near - z * (far - near));
}

// expect values in the range [near..far]
float normalizeDepth(float depth) {
  // depth values are returned in the range [0..1]
  return (depth - near) / (far - near);
}

void main() {
  float linear_depth = linearizeDepth(gl_FragCoord.z);
  float depth = normalizeDepth(linear_depth);
  gl_FragColor = vec4(vec3(depth), 1.0);
}