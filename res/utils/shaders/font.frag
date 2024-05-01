#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float outlineLen;
uniform vec4 outlineColor;

varying vec4 v_color;
varying vec2 v_texCoord;

const float smoothing = 1.0/16.0;

void main() {
    float distance = texture2D(u_texture, v_texCoord).a;
    vec4 color;
    float alpha;
    if (outlineLen == 0.5) {
        color = v_color;
        alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    } else {
        float outlineFactor = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
        color = mix(outlineColor, v_color, outlineFactor);
        alpha = smoothstep(outlineLen - smoothing, outlineLen + smoothing, distance);
    }
    gl_FragColor = vec4(color.rgb, color.a * alpha);
}