#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform LOWP vec4 u_dustColor;
uniform float u_time;
uniform float u_dustDepthPrevious;
uniform float u_dustDepthCurrent;
uniform float u_dustDepthNext;

varying vec2 v_texCoords;
varying float v_horizontalPositionInQuad;

vec3 random3(vec3 c) {
    float j = 4096.0*sin(dot(c,vec3(17.0, 59.4, 15.0)));
    vec3 r;
    r.z = fract(512.0*j);
    j *= .125;
    r.x = fract(512.0*j);
    j *= .125;
    r.y = fract(512.0*j);
    return r-0.5;
}

const float F3 =  0.3333333;
const float G3 =  0.1666667;
float noise(vec3 p) {

    vec3 s = floor(p + dot(p, vec3(F3)));
    vec3 x = p - s + dot(s, vec3(G3));

    vec3 e = step(vec3(0.0), x - x.yzx);
    vec3 i1 = e*(1.0 - e.zxy);
    vec3 i2 = 1.0 - e.zxy*(1.0 - e);

    vec3 x1 = x - i1 + G3;
    vec3 x2 = x - i2 + 2.0*G3;
    vec3 x3 = x - 1.0 + 3.0*G3;

    vec4 w, d;

    w.x = dot(x, x);
    w.y = dot(x1, x1);
    w.z = dot(x2, x2);
    w.w = dot(x3, x3);

    w = max(0.6 - w, 0.0);

    d.x = dot(random3(s), x);
    d.y = dot(random3(s + i1), x1);
    d.z = dot(random3(s + i2), x2);
    d.w = dot(random3(s + 1.0), x3);

    w *= w;
    w *= w;
    d *= w;

    return dot(d, vec4(52.0));
}

void main() {
    float dustHeight=0.0;
    if (v_horizontalPositionInQuad<0.5) {
        dustHeight = mix(u_dustDepthPrevious, u_dustDepthCurrent, v_horizontalPositionInQuad*2.0);
    } else {
        dustHeight = mix(u_dustDepthCurrent, u_dustDepthNext, v_horizontalPositionInQuad*2.0-1.0);
    }

    float noise = noise(vec3(v_texCoords*2.0, u_time/4.0));
    float alpha=mix(0.3, 1.0, noise);
    if (v_texCoords.y>=dustHeight) {
        alpha = 0.0;
    } else {
        alpha *= (1.0-v_texCoords.y/dustHeight)*(1.0-v_texCoords.y/dustHeight);
        //alpha *= (1-v_texCoords.y/dustHeight) * (1-v_texCoords.y*dustHeight);

    }

    gl_FragColor = vec4(u_dustColor.rgb, alpha);
}