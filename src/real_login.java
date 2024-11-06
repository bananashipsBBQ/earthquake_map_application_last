import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class real_login {
    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton realloginButton;
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
    static final String USER = "root";
    static final String PASS = "123456";

    public static boolean authenticate(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 准备 SQL 查询，查询数据库中的用户名和密码
            String sql = "SELECT * FROM mapusers WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // 执行查询
            rs = pstmt.executeQuery();

            // 检查是否有匹配的结果
            return rs.next();  // 如果有结果，说明用户名和密码匹配
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return false;
    }

    public real_login(){
    // 为登录按钮添加点击事件监听器
        realloginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 这里是登录按钮的处理逻辑
                String username = textField1.getText();
                String password = new String(passwordField1.getPassword());
                // 判断用户名和密码，假设验证通过跳转到登录成功的窗口
                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(panel1, "登录成功!");
                    // 登录成功后可以跳转到主界面
                    // 假设 MainWindow 是你跳转后的窗口
                    EarthquakeMapApp.main(null);
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(panel1);
                    currentFrame.dispose();//关闭当前窗口
                } else {
                    JOptionPane.showMessageDialog(panel1, "登录失败！");
                }
            }
        });
    }


    public static void main() {
        FlatDarkLaf.setup();
        JFrame frame = new JFrame("real_login");
        frame.setBounds(500, 500, 500, 500);
        frame.setContentPane(new real_login().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
