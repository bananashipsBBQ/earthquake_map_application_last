import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class real_register {

    private JButton registerButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JPanel registerJpanel;
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
    static final String USER = "root";
    static final String PASS = "123456";

    public real_register() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username1 = textField1.getText();
                String password1 = new String(passwordField1.getPassword());
                String password2 = new String(passwordField2.getPassword());

                // 检查密码是否一致
                if (!password1.equals(password2)) {
                    JOptionPane.showMessageDialog(registerJpanel, "密码不一致！请检查！");
                    return;
                }

                Connection conn = null;
                PreparedStatement pstmt = null;
                try {
                    Class.forName(JDBC_DRIVER);  // 注册 JDBC 驱动
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);  // 连接数据库

                    // 插入数据
                    String sql = "INSERT INTO mapusers (username, password) VALUES (?, ?)";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username1);
                    pstmt.setString(2, password1);
                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(registerJpanel, "注册成功!");
                        login_window.main();
                        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(registerJpanel);
                        currentFrame.dispose();
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(registerJpanel, "注册失败!");
                } finally {
                    try {
                        if (pstmt != null) pstmt.close();
                        if (conn != null) conn.close();//关闭数据库链接
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
            }
        });
    }


    public static void main() {
        FlatDarkLaf.setup();
        JFrame frame = new JFrame("real_register");
        frame.setBounds(500, 500, 500, 500);
        frame.setContentPane(new real_register().registerJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
