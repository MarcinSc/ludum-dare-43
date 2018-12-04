#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_sourceTexture;
uniform float u_factor;
uniform vec2 u_size;
uniform vec2 u_random;

varying vec2 v_position;

float rand(vec2 n) {
  return 0.5 + 0.5 *
     fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main() {
    float grain = rand(floor(v_position/u_size) * u_size + u_random);
    vec4 grainColor = vec4(grain, grain, grain, 1.0);
    gl_FragColor = mix(texture2D(u_sourceTexture, v_position), grainColor, u_factor);
}