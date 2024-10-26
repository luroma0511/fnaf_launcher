
varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform float u_cameraX;
uniform float u_distortionAmount;

void main() {
    float centerX = 0.5; // Center of the texture in X coordinates
    float centerY = 0.5; // Center of the texture in Y coordinates

    // Calculate the offset from the center in the x-axis
    float offsetX = v_texCoord.x - centerX; // Offset from the center X

    // Initialize scaling factors for both sides
    float leftScaleFactor = 1.0;
    float rightScaleFactor = 1.0;

    // Determine the scale factors based on the camera's x position
    if (u_cameraX < 0.0) { // Camera moving left
        leftScaleFactor = 1.0 + (abs(offsetX) * -u_cameraX * u_distortionAmount); // Scale down left side
        rightScaleFactor = 1.0 - (abs(offsetX) * -u_cameraX * u_distortionAmount); // Scale up right side
    } else if (u_cameraX > 0.0) { // Camera moving right
        leftScaleFactor = 1.0 - (abs(offsetX) * u_cameraX * u_distortionAmount); // Scale up left side
        rightScaleFactor = 1.0 + (abs(offsetX) * u_cameraX * u_distortionAmount); // Scale down right side
    }

    // Ensure we don't scale negatively
    leftScaleFactor = max(leftScaleFactor, 0.0);
    rightScaleFactor = max(rightScaleFactor, 0.0);

    // Adjust the texture coordinates based on the scale factors
    vec2 distortedTexCoord = v_texCoord;

    // Calculate the offset from the center in the y-axis
    float offsetY = v_texCoord.y - centerY; // Offset from the center Y

    // Adjust the texture coordinates based on the x offset
    if (offsetX < 0.0) { // Left side
        // Scale down the left side
        distortedTexCoord.y = centerY + (offsetY * leftScaleFactor); // Moving down for the left side
    } else { // Right side
        // Scale down the right side
        distortedTexCoord.y = centerY + (offsetY * rightScaleFactor); // Moving up for the right side
    }

    // Clamp the y coordinates to ensure they stay within [0, 1]
    distortedTexCoord.y = clamp(distortedTexCoord.y, 0.0, 1.0);

    // Keep x coordinate the same
    distortedTexCoord.x = v_texCoord.x;

    // Sample the texture with distorted coordinates
    gl_FragColor = texture(u_texture, distortedTexCoord);
}