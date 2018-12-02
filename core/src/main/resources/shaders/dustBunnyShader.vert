attribute vec4 a_position;

uniform mat4 u_projTrans;
uniform vec4 u_coordinates;

varying vec2 v_texCoords;
varying vec2 v_screenPosition;

void main() {
    v_texCoords = a_position.xy;
    vec4 screenPosition = u_projTrans * vec4(u_coordinates.xy + u_coordinates.zw * a_position, a_position.z, a_position.w);
    v_screenPosition = screenPosition;
    gl_Position = screenPosition;
}