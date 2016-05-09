package com.ding.trans.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.text.StrTokenizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ClientUtil {

    public static ImageView createIcon(String imageName, int fitSize) {
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        icon.setFitWidth(fitSize);
        icon.setImage(new Image(Config.getResource(imageName).toExternalForm()));
        return icon;
    }

    public static void moveStageToOwnerCenter(Stage stage) {
        Window owner = stage.getOwner();
        stage.setX(owner.getX() + owner.getWidth() / 2 - stage.getWidth() / 2);
        stage.setY(owner.getY() + owner.getHeight() / 2 - stage.getHeight() / 2);
    }

    public static <V> Task<V> createAsyncTask(Callable<V> callable) {
        Task<V> task = new Task<V>() {

            @Override
            protected V call() throws Exception {
                return callable.call();
            }

        };
        return task;
    }

    public static void runAsyncTask(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void onAsyncTaskSuccess(Worker<?> worker, Runnable handler, String msg, Object... objs) {
        EventHandler<WorkerStateEvent> eventHandler = e -> {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    if (handler != null) {
                        handler.run();
                    }
                    for (Object obj : objs) {
                        if (obj instanceof Node) {
                            ((Node) obj).setDisable(false);
                            ((Node) obj).requestFocus();
                        } else if (obj instanceof Window) {
                            ((Window) obj).hide();
                        }
                    }
                    if (msg != null) {
                        Main.showSuccessMsg(msg);
                    }
                }

            });
        };
        if (worker instanceof Task<?>) {
            ((Task<?>) worker).setOnSucceeded(eventHandler);
        } else if (worker instanceof Service<?>) {
            ((Service<?>) worker).setOnSucceeded(eventHandler);
        }
    }

    public static void onAsyncTaskFailure(Worker<?> worker, Runnable handler, Object... objs) {
        EventHandler<WorkerStateEvent> eventHandler = e -> {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    for (Object obj : objs) {
                        if (obj instanceof Node) {
                            ((Node) obj).setDisable(false);
                            ((Node) obj).requestFocus();
                        } else if (obj instanceof Window) {
                            ((Window) obj).hide();
                        }
                    }

                    Throwable t = e.getSource().getException();
                    String error = null;
                    if (t != null) {
                        error = t.getLocalizedMessage();
                        if (error == null || error.isEmpty()) {
                            error = t.getClass().getName();
                        }
                        Main.showFailureMsg(error, handler);
                    } else {
                        // Main.showFailureMsg("UI更新遇到问题", handler);
                    }
                }

            });
        };
        if (worker instanceof Task<?>) {
            ((Task<?>) worker).setOnFailed(eventHandler);
        } else if (worker instanceof Service<?>) {
            ((Service<?>) worker).setOnFailed(eventHandler);
        }
    }

    public static Button createImageButton(ImageView icon) {
        Button imageBtn = new Button(null, icon);
        double w = icon.getFitWidth();
        double h = icon.getFitHeight();
        imageBtn.setMinSize(w, h);
        imageBtn.setMaxSize(w, h);
        imageBtn.setFocusTraversable(false);
        imageBtn.setCursor(Cursor.HAND);
        DropShadow dropShadow = new DropShadow(10, 0, 0, Color.rgb(50, 50, 50, .588));
        imageBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> imageBtn.setEffect(dropShadow));
        imageBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> imageBtn.setEffect(null));
        imageBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> dropShadow.setRadius(5));
        imageBtn.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> dropShadow.setRadius(10));
        return imageBtn;
    }

    public static <S, T> TableColumn<S, T> createTableColumn(String colName, String propName) {
        TableColumn<S, T> col = new TableColumn<>(colName);
        col.setCellValueFactory(new PropertyValueFactory<S, T>(propName));
        col.setUserData(propName);
        return col;
    }

    public static TilePane createControlBar(List<Button> buttons) {
        buttons.forEach(b -> {
            String userData = (String) b.getUserData();
            if (userData != null) {
                b.setGraphic(ClientUtil.createIcon(userData + ".png", 16));
            }
            b.setMinWidth(Button.USE_PREF_SIZE);
            b.setMaxWidth(Double.MAX_VALUE);
        } );

        TilePane controlBar = new TilePane();
        controlBar.setHgap(10.0);
        controlBar.setPrefColumns(buttons.size());
        controlBar.setMinWidth(TilePane.USE_PREF_SIZE);
        controlBar.setMinHeight(TilePane.USE_PREF_SIZE);
        controlBar.setMaxHeight(TilePane.USE_PREF_SIZE);
        controlBar.setAlignment(Pos.BASELINE_CENTER);
        controlBar.getChildren().addAll(buttons);
        return controlBar;
    }

    public static GridPane createFormLayout(ColumnConstraints cc1, ColumnConstraints cc2) {
        GridPane formLayout = new GridPane();
        formLayout.setHgap(20);
        formLayout.setVgap(10);
        formLayout.setAlignment(Pos.CENTER);
        cc2.setHgrow(Priority.ALWAYS);
        formLayout.getColumnConstraints().addAll(cc1, cc2);
        return formLayout;
    }

    public static CheckBox addCheckBoxInputToForm(GridPane form, String name, int row) {
        Label label = new Label(name);
        CheckBox input = new CheckBox();
        form.add(label, 0, row);
        form.add(input, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(input, HPos.LEFT);
        return input;
    }

    public static RadioButton addRadioInputToForm(GridPane form, String name, int row) {
        Label label = new Label(name);
        RadioButton input = new RadioButton();
        form.add(input, 0, row);
        form.add(label, 1, row);
        GridPane.setHalignment(input, HPos.RIGHT);
        GridPane.setHalignment(label, HPos.LEFT);
        return input;
    }

    public static Label addLabelValueToForm(GridPane form, String name, String initValue, int row) {
        Label label = new Label(name);
        Label value = new Label(initValue == null ? "" : initValue);
        form.add(label, 0, row);
        form.add(value, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(value, HPos.LEFT);
        return value;
    }

    public static TextField addTextInputToForm(GridPane form, String name, String prompt, int row) {
        Label label = new Label(name);
        TextField input = new TextField();
        if (prompt != null) {
            input.setPromptText(prompt);
        }
        form.add(label, 0, row);
        form.add(input, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(input, HPos.LEFT);
        return input;
    }

    public static PasswordField addPasswordInputToForm(GridPane form, String name, String prompt, int row) {
        Label label = new Label(name);
        PasswordField input = new PasswordField();
        if (prompt != null) {
            input.setPromptText(prompt);
        }
        form.add(label, 0, row);
        form.add(input, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(input, HPos.LEFT);
        return input;
    }

    public static <T> ComboBox<T> addComboBoxInputToForm(GridPane form, String name, String prompt, int row) {
        Label label = new Label(name);
        ComboBox<T> input = new ComboBox<>();
        input.setPrefWidth(form.getColumnConstraints().get(1).getPrefWidth());
        if (prompt != null) {
            input.setPromptText(prompt);
        }
        form.add(label, 0, row);
        form.add(input, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(input, HPos.LEFT);
        return input;
    }

    public static <T> ListView<T> addListViewInputToForm(GridPane form, String name, List<T> options, int row) {
        Label label = new Label(name);
        ListView<T> input = new ListView<>();
        input.setPrefWidth(form.getColumnConstraints().get(1).getPrefWidth());
        if (options != null) {
            input.getItems().addAll(options);
        }
        form.add(label, 0, row);
        form.add(input, 1, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(input, HPos.LEFT);
        return input;
    }

    public static void setHighlighted(Node node, boolean b) {
        List<String> styleClasses = node.getStyleClass();
        if (b) {
            if (!styleClasses.contains("dashed-red-border")) {
                styleClasses.add("dashed-red-border");
            }
        } else {
            styleClasses.remove("dashed-red-border");
        }
    }

    public static int convertStrToInteger(String s) {
        try {
            return (s == null || s.isEmpty()) ? 0 : Integer.parseInt(s);
        } catch (Exception e) {
            throw new RuntimeException(s);
        }
    }

    public static double convertStrToDouble(String s) {
        try {
            return (s == null || s.isEmpty()) ? 0.0 : Double.parseDouble(s);
        } catch (Exception e) {
            throw new RuntimeException(s);
        }
    }

    public static boolean convertStrToBoolean(String s) {
        try {
            return (s == null || s.isEmpty()) ? false : Boolean.parseBoolean(s);
        } catch (Exception e) {
            throw new RuntimeException(s);
        }
    }

    public static Map<String, String> convertStrToMap(String s) {
        Map<String, String> m = new HashMap<String, String>();
        StrTokenizer st = StrTokenizer.getCSVInstance(s);
        try {
            while (st.hasNext()) {
                String token = st.nextToken();
                if (!token.contains("=")) {
                    m.put(token.trim(), "");
                } else {
                    int i = token.lastIndexOf('=');
                    String k = token.substring(0, i);
                    String v = token.substring(i + 1);
                    m.put(k.trim(), v.trim());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(s);
        }
        return m;
    }

    public static String convertMapToStr(Map<String, String> m) {
        if (m == null || m.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : m.entrySet()) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            String k = entry.getKey();
            String v = entry.getValue();
            sb.append(k).append(v.isEmpty() ? "" : "=" + v);
        }
        return sb.toString();
    }

    public static List<String> convertStrToList(String s) {
        StrTokenizer st = StrTokenizer.getCSVInstance(s);
        try {
            return Arrays.asList(st.getTokenArray());
        } catch (Exception e) {
            throw new RuntimeException(s);
        }
    }

    public static String convertListToStr(List<String> l) {
        if (l == null || l.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String entry : l) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append('"').append(entry).append('"');
        }
        return sb.toString();
    }

    public static long getLongValue(Object o) {
        Long l = o instanceof Integer ? Long.valueOf(o.toString()) : (Long) o;
        return l == null ? 0L : l.longValue();
    }

    public static String getStringValue(Object o) {
        return o == null ? "" : o.toString();
    }

    public static int getIntegerValue(Object o) {
        Integer v = o instanceof Long ? Integer.valueOf(o.toString()) : (Integer) o;
        return v == null ? 0 : v.intValue();
    }

    public static double getDoubleValue(Object o) {
        Double v = (Double) o;
        return v == null ? 0.0 : v.doubleValue();
    }

    public static boolean getBooleanValue(Object o) {
        Boolean v = (Boolean) o;
        return v == null ? false : v.booleanValue();
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ObservableMap<K, V> getMapValue(Object o) {
        ObservableMap<K, V> v = FXCollections.observableHashMap();
        if (o != null) {
            v.putAll((Map<K, V>) o);
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    public static <E> ObservableList<E> getListValue(Object o) {
        ObservableList<E> v = FXCollections.observableArrayList();
        if (o != null) {
            v.addAll((List<E>) o);
        }
        return v;
    }

    public static <E> String join(List<E> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (E item : list) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(item.toString());
        }
        return sb.toString();
    }

    public static void copyFile(File source, File target) throws IOException {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(target);
        byte[] buffer = new byte[1024];
        int n;
        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
        } finally {
            in.close();
            out.close();
        }
    }

    public static String normPath(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String capitalize(String text) {
        if (text == null) {
            return null;
        }
        if (text.isEmpty()) {
            return text;
        }

        String c = text.substring(0, 1).toUpperCase();
        if (text.length() == 1) {
            return c;
        } else {
            return c + text.substring(1);
        }
    }

    public static String compress(String s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(s.getBytes("UTF-8"));
        gzip.close();
        return new String(Base64.getEncoder().encode(out.toByteArray()), "UTF-8");
    }

    public static String decompress(String s) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(s));
        GZIPInputStream gzip = new GZIPInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = gzip.read(buf)) >= 0) {
            out.write(buf, 0, i);
        }
        gzip.close();
        return out.toString("UTF-8");
    }

    public static String cipherSimply(String s) {
        try {
            byte[] bytes = s.getBytes("UTF-8");
            byte[] encoded = Base64.getEncoder().encode(bytes);
            for (int i = 0; i < encoded.length - 3; i += 2) {
                byte b = encoded[i];
                encoded[i] = encoded[i + 1];
                encoded[i + 1] = b;
            }
            return new String(encoded, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decipherSimply(String s) {
        try {
            byte[] encoded = s.getBytes("UTF-8");
            for (int i = 0; i < encoded.length - 3; i += 2) {
                byte b = encoded[i];
                encoded[i] = encoded[i + 1];
                encoded[i + 1] = b;
            }
            byte[] bytes = Base64.getDecoder().decode(encoded);
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
