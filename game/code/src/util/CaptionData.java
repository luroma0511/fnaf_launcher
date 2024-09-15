package util;

import java.util.List;

public record CaptionData(short id, int width, int height, List<String> captions){}