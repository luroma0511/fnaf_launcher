

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;

void main() {

    vec2 coord = v_texCoord;
    float pixelDistanceX = distance(v_texCoord.x, 0.5);
    float pixelDistanceY = distance(v_texCoord.y, 0.5);

    float offsetx = (pixelDistanceX * 0.2) * pixelDistanceY;

    float dirx;
    if (v_texCoord.y <= 0.5) {
        dirx = 1.0;
    } else {
        dirx = -1.0;
    }

    coord = vec2(v_texCoord.x, v_texCoord.y + pixelDistanceX * (offsetx * 3.0 * dirx));

    vec4 sampledColor = texture2D(u_texture, coord);

    gl_FragColor = sampledColor * v_color;
}