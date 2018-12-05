#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.1415926535897932384626433832795

uniform sampler2D u_sourceTexture;
uniform vec2 u_position;
uniform float u_distance;
uniform float u_size;
uniform float u_alpha;
uniform float u_heightToWidth;

varying vec2 v_position;

void main() {
    vec2 pixelPosition = v_position - 0.5;
    pixelPosition.y*=u_heightToWidth;
    float distance = distance(pixelPosition, u_position);
    vec2 uvTexture = v_position;

    if (u_distance-u_size<distance && distance<u_distance+u_size) {
        // We're inside the shockwave
        float sinDist = sin(PI*(distance - u_distance)/u_size );
        vec2 textureModified = normalize(pixelPosition -u_position) * sinDist*(u_size/2.0);
        textureModified.y*=u_heightToWidth;
        uvTexture += textureModified;
    }

    if (u_alpha >= 1.0)
        gl_FragColor = texture2D(u_sourceTexture, uvTexture);
    else
        gl_FragColor = mix(texture2D(u_sourceTexture, v_position), texture2D(u_sourceTexture, uvTexture), u_alpha);
}