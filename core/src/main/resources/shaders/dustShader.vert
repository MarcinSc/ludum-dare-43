attribute vec4 a_position;

uniform mat4 u_projTrans;
uniform vec4 u_coordinates;
uniform vec4 u_platformProgress;

varying vec2 v_texCoords;
varying float v_horizontalPositionInQuad;

void main() {
    v_texCoords = u_platformProgress.xy + u_platformProgress.zw * a_position.xy;
    v_horizontalPositionInQuad = a_position.x;
    gl_Position = u_projTrans * vec4(u_coordinates.xy + u_coordinates.zw*a_position.xy, a_position.z, a_position.w);
}