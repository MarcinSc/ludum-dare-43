attribute vec4 a_position;

uniform mat4 u_projTrans;
uniform vec4 u_coordinates;
uniform vec4 u_realTexCoords;

varying vec2 v_texCoords;
varying vec2 v_screenPosition;
varying vec2 v_realTexCoords;

void main() {
    v_texCoords = a_position.xy;
    v_realTexCoords = u_realTexCoords.xy + u_realTexCoords.zw * a_position.xy;
    vec4 screenPosition = u_projTrans * vec4(u_coordinates.xy + u_coordinates.zw * a_position.xy, a_position.z, a_position.w);
    v_screenPosition = screenPosition.xy;
    gl_Position = screenPosition;
}