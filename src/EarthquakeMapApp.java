import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EarthquakeMapApp {

    private JFrame frame;
    private JPanel mapPanel;
    private JTextArea earthquakeInfoArea;
    private List<EarthquakeWaypoint> waypoints;
    private double zoomLevel = 1.0; // 初始缩放级别，1.0为正常尺寸
    private double mapCenterX = 0.0; // 地图中心X坐标（经度）
    private double mapCenterY = 0.0; // 地图中心Y坐标（纬度）

    private Image earthImage;  // 用于显示背景地球图像

    public EarthquakeMapApp() {
        initialize();
        loadEarthquakeData();  // 初始加载地震数据
        startDataRefresh();  // 开始定时更新地震数据
    }

    private void initialize() {
        // 主窗口设置
        frame = new JFrame("Earthquake Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());

        // 左侧地震信息面板
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(300, 800));
        leftPanel.setLayout(new BorderLayout());

        earthquakeInfoArea = new JTextArea();
        earthquakeInfoArea.setEditable(false);
        leftPanel.add(new JScrollPane(earthquakeInfoArea), BorderLayout.CENTER);

        frame.add(leftPanel, BorderLayout.WEST);

        // 右侧地图面板
        mapPanel = new MapPanel();
        mapPanel.setBackground(Color.LIGHT_GRAY);
        waypoints = new ArrayList<>(); // 初始化为空

        // 加载地球图像背景
        try {
            earthImage = ImageIO.read(new File("F:\\edge_downloads\\earth_image.jpg"));  // 这里你需要提供一个地球图像
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 为标记添加点击事件
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedPoint = e.getPoint();
                for (EarthquakeWaypoint waypoint : waypoints) {
                    Point2D point = geoToPixel(waypoint.getPosition());
                    if (point.distance(clickedPoint) < 10) {
                        displayEarthquakeInfo(waypoint);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // 记录鼠标按下的位置，用于拖拽地图
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mapPanel.setCursor(Cursor.getDefaultCursor());
            }
        });

        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 鼠标拖动时计算地图偏移
                int dx = e.getX() - mapPanel.getWidth() / 2;
                int dy = e.getY() - mapPanel.getHeight() / 2;

                mapCenterX -= dx / (100.0 * zoomLevel);  // 经度的偏移量
                mapCenterY += dy / (100.0 * zoomLevel);  // 纬度的偏移量

                mapPanel.repaint();
            }
        });

        // 添加鼠标滚轮缩放功能
        mapPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();  // 放大
                } else {
                    zoomOut();  // 缩小
                }
            }
        });

        frame.add(mapPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 放大地图
    private void zoomIn() {
        zoomLevel *= 1.1;  // 放大10%
        mapPanel.repaint();
    }

    // 缩小地图
    private void zoomOut() {
        zoomLevel /= 1.1;  // 缩小10%
        mapPanel.repaint();
    }

    // 将地理位置转换为像素位置
    private Point2D geoToPixel(GeoPosition position) {
        int x = (int) ((position.getLongitude() - mapCenterX + 180) * (mapPanel.getWidth() / 360.0) * zoomLevel);
        int y = (int) ((90 - position.getLatitude() - mapCenterY) * (mapPanel.getHeight() / 180.0) * zoomLevel);
        return new Point2D.Double(x, y);
    }

    // 显示地震详细信息
    private void displayEarthquakeInfo(EarthquakeWaypoint waypoint) {
        earthquakeInfoArea.setText("Location: " + waypoint.getLocationName() + "\n" +
                "Magnitude: " + waypoint.getMagnitude() + "\n" +
                "Depth: " + waypoint.getDepth() + " km\n" +
                "Time: " + waypoint.getTime());
    }

    // 加载地震数据（从 API 获取真实数据）
    private void loadEarthquakeData() {
        waypoints.clear(); // 清空之前的数据
        try {
            String urlString = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // 解析 JSON 数据
            JsonElement jsonElement = JsonParser.parseString(content.toString());
            JsonArray features = jsonElement.getAsJsonObject().getAsJsonArray("features");

            for (JsonElement feature : features) {
                var properties = feature.getAsJsonObject().getAsJsonObject("properties");

                // 确保字段存在且不为 null
                double magnitude = properties.has("mag") && !properties.get("mag").isJsonNull() ? properties.get("mag").getAsDouble() : 0.0;
                String location = properties.has("place") && !properties.get("place").isJsonNull() ? properties.get("place").getAsString() : "Unknown Location";
                long time = properties.has("time") && !properties.get("time").isJsonNull() ? properties.get("time").getAsLong() : 0;
                double depth = properties.has("depth") && !properties.get("depth").isJsonNull() ? properties.get("depth").getAsDouble() : 0.0;

                JsonArray coordinates = feature.getAsJsonObject().getAsJsonObject("geometry").getAsJsonArray("coordinates");
                // 获取坐标，检查是否存在
                double longitude = coordinates.size() > 1 ? coordinates.get(0).getAsDouble() : 0.0;
                double latitude = coordinates.size() > 0 ? coordinates.get(1).getAsDouble() : 0.0;

                GeoPosition position = new GeoPosition(latitude, longitude);
                waypoints.add(new EarthquakeWaypoint(position, location, magnitude, depth, new java.util.Date(time).toString()));
            }
            mapPanel.repaint(); // 更新地图
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定时更新地震数据
    private void startDataRefresh() {
        new Timer(60000, e -> loadEarthquakeData()).start(); // 每分钟更新一次
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EarthquakeMapApp::new);
    }

    // 自定义地图面板
    class MapPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 绘制背景地图（假设已经加载了地球图像）
            if (earthImage != null) {
                int imgWidth = earthImage.getWidth(null);
                int imgHeight = earthImage.getHeight(null);
                g.drawImage(earthImage, 0, 0, (int)(getWidth() * zoomLevel), (int)(getHeight() * zoomLevel), this);
            }

            // 绘制地震标记
            g.setColor(Color.RED);
            for (EarthquakeWaypoint waypoint : waypoints) {
                Point2D point = geoToPixel(waypoint.getPosition());
                g.fillOval((int) point.getX() - 5, (int) point.getY() - 5, 10, 10);
            }
        }
    }

    // 自定义地震标记类
    class EarthquakeWaypoint {
        private GeoPosition position;
        private String locationName;
        private double magnitude;
        private double depth;
        private String time;

        public EarthquakeWaypoint(GeoPosition position, String locationName, double magnitude, double depth, String time) {
            this.position = position;
            this.locationName = locationName;
            this.magnitude = magnitude;
            this.depth = depth;
            this.time = time;
        }

        public GeoPosition getPosition() {
            return position;
        }

        public String getLocationName() {
            return locationName;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public double getDepth() {
            return depth;
        }

        public String getTime() {
            return time;
        }
    }

    // 自定义地理坐标类
    class GeoPosition {
        private double latitude;
        private double longitude;

        public GeoPosition(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
