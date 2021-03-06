package com.jemetech.jeme_ifbg.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jemetech.jeme_ifbg.ISaver;
import com.jemetech.jeme_ifbg.Sensor;
import com.jemetech.jeme_ifbg.Connection;
import com.jemetech.jeme_ifbg.ConnectionListerner;
import com.jemetech.jeme_ifbg.ConnectionManager;
import com.jemetech.jeme_ifbg.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Lei on 11/24/2016.
 */

public class MainActivity extends Activity {

    private boolean showingLogin = false;
    private ConnectionManager connectionManager = ConnectionManager.getInstance();
//    private Canvas canvas;
    private Sensor selectedSensor;
    private Connection connection;
//    private Bitmap bitmap;
//    private DrawingView view;
    private LineChartView view;
//    private Paint paint = new Paint();
    private Sensor[] sensors;
    private ListView list;
    private boolean paused = true;

    private static final int XSTART = 30;
    static final String key = "addr";
    static final String host = "host";
    static final String port = "port";
    private Button saveButton;
    private Button finishButton;
    private Saver saver;
    private File savingFile;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    private LineChartData data;
    private Line line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        saveButton = (Button) findViewById(R.id.save);
        finishButton = (Button) findViewById(R.id.finish);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = Environment.getExternalStorageDirectory();
                Date date = new Date();
                savingFile = new File(dir, df.format(date) + ".txt");
                try {
                    savingFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(savingFile);
                    saver = new Saver(fos);
                    connection.setSaver(saver);
                    saveButton.setEnabled(false);
                    saveButton.setText(R.string.saving);
                    finishButton.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Create file fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saver.removing = true;
                saver = null;
                Toast.makeText(v.getContext(), "File "+savingFile.getName()+" saved!", Toast.LENGTH_SHORT).show();
                savingFile = null;
                saveButton.setText(R.string.save);
                saveButton.setEnabled(true);
                finishButton.setEnabled(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (connectionManager.getConnection() == null) {
//            if (!showingLogin) {
//                LoginDialog dialog = new LoginDialog();
//                dialog.setActivity(this);
//                dialog.show(getFragmentManager(), null);
//                showingLogin = true;
//            }
//        }
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginDialog dialog = new LoginDialog();
        dialog.setActivity(this);
        dialog.show(getFragmentManager(), null);
        showingLogin = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connection != null)
                connection.close();
    }

    private void activeSensor(Sensor sensor) {
//        boolean start = false;
//        if (selectedSensor == null)
//            start = true;
        selectedSensor = sensor;
//        selectedSensor.debug(true);
//        if (start) {
//            view = (DrawingView) findViewById(R.id.surface);
//            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
//            canvas = new Canvas(bitmap);
//            view.setDrawer(new DrawingView.Drawer() {
//                @Override
//                public void draw(Canvas canvas) {
//                    canvas.drawBitmap(bitmap, 0, 0, null);
//                }
//            });
//        }
    }

    private void draw() {
        if(selectedSensor == null)
            return;
        if(!selectedSensor.ready())
            return;
//        float range = selectedSensor.max - selectedSensor.min;
//        float base = selectedSensor.min + range / 2f;
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.LTGRAY);
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
//        Path path = new Path();
//        Iterator<Sensor.Data> data = selectedSensor.getData();
//        if (!data.hasNext())
//            return;
//        int width = canvas.getWidth();
//        int height = canvas.getHeight();
//        float scale = (height - 50) / range;
//        int startX = width-XSTART;
//        int startY = height/2;
//        Sensor.Data d = data.next();
//        Sensor.Data current = d;
//        long last = d.second;
//        float value = d.value;
//        path.moveTo(startX, startY-(value-base)*scale);
//        float max = value;
//        float min = value;
//        while (data.hasNext()) {
//            d = data.next();
//            long interv = last - d.second;
//            if (interv <= 0)
//                interv += 60000;
//            startX -= interv/50f;
//            path.lineTo(startX, startY - (d.value-base)*scale);
//            last = d.second;
//            if(d.value > max)
//                max = d.value;
//            if(d.value < min)
//                min = d.value;
//            if(startX < 0)
//                break;
//        }
//        selectedSensor.max = max;
//        selectedSensor.min = min;
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.BLACK);
//        paint.setAntiAlias(true);
//        canvas.drawPath(path, paint);
//
//        paint.setAntiAlias(false);
//        paint.setColor(Color.DKGRAY);
//        int y = height-XSTART;
//        canvas.drawLine(width, y, 0, y, paint);
//        int x = width-XSTART;
//        canvas.drawLine(x, y-5, x, y, paint);
//        last = current.second;
//        int second = (int) (last/1000);
//        canvas.drawText(""+second+'.'+(last-second*1000), x, y+5, paint);
//        canvas.drawLine(XSTART, 0, XSTART, height, paint);
//        canvas.drawText(""+base, 0, startY, paint);
//        path = new Path();
//        path.moveTo(width, startY);
//        path.lineTo(0, startY);
//        PathEffect pe = new DashPathEffect(new float[]{3, 5}, 0);
//        paint.setPathEffect(pe);
//        canvas.drawPath(path, paint);
//        paint.setPathEffect(null);
        line.setValues(selectedSensor.getCData());
//        data.setBaseValue(selectedSensor.min);
        view.setLineChartData(data);
//        line.set
    }

    public void onLoginDismiss(LoginDialog dialog) {
        showingLogin = false;
        if(!dialog.isConnect())
            finish();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ProgressBar pb = new ProgressBar(builder.getContext(), null, android.R.attr.progressBarStyle);
            final AlertDialog aDialog = builder.setTitle(R.string.connecting).setView(pb).setPositiveButton(R.string.ok, null).setCancelable(false).show();
//            FrameLayout fl = (FrameLayout) aDialog.findViewById(android.R.id.custom);
//            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyle);
//            fl.addView(pb, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            View busyicon = aDialog.getLayoutInflater().inflate(R.layout.busyicon, fl, false);
//            fl.addView(busyicon, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Button button = aDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setEnabled(false);
            connectionManager.connect(dialog.getTheHost(), dialog.getPort(), new ConnectionListerner() {
                @Override
                public void onSuccess() {
//                    view = (DrawingView) findViewById(R.id.surface);
                    view = (LineChartView) findViewById(R.id.lineview);
                    line = new Line();
                    line.setHasLines(true);
                    line.setHasPoints(false);
                    line.setStrokeWidth(1);
                    data = new LineChartData(Arrays.asList(line));
                    Axis axisX = new Axis();
                    Axis axisY = new Axis().setHasLines(true);
                    data.setAxisXBottom(axisX);
                    data.setAxisYLeft(axisY);
//                    bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
//                    canvas = new Canvas(bitmap);
//                    view.setDrawer(new DrawingView.Drawer() {
//                        @Override
//                        public void draw(Canvas canvas) {
//                            canvas.drawBitmap(bitmap, 0, 0, null);
//                        }
//                    });
                    connection = connectionManager.getConnection();

                    connection.addUpdateListener(new Connection.UpdateListener() {
                        @Override
                        public void update() {
                            if (sensors == null) {
                                sensors = connection.getSensors();
                                list = (ListView) findViewById(R.id.channellist);
                                list.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.setAdapter(new ArrayAdapter<Sensor>(MainActivity.this, R.layout.itemview, sensors));
                                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                activeSensor((Sensor) parent.getAdapter().getItem(position));
                                                draw();
                                            }
                                        });
                                    }
                                });
                                list.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.setItemChecked(0, true);
                                        list.performItemClick(list.getChildAt(0), 0, 0);
                                    }
                                });
                            }
                            draw();
                            list.post(new Runnable() {
                                @Override
                                public void run() {
                                    int first = list.getFirstVisiblePosition();
                                    int last = list.getLastVisiblePosition();
                                    int j = 0;
                                    for (int i = first; i <= last; i++) {
                                        ((TextView) list.getChildAt(j++)).setText(sensors[i].toString());
                                    }
                                }
                            });
                        }
                    });
                    aDialog.dismiss();
                }

                @Override
                public void onFail(Reason reason) {

                }
            }, new Sensor.Converter<PointValue>() {
                public PointValue convert(Sensor.Data data) {
                    return new PointValue(data.minute*60000+data.second, data.value);
                }
            });
        }
    }
    private static class Saver implements ISaver {
        private final OutputStream os;
        private final BufferedWriter writer;
        private boolean removing = false;
        private boolean newline = true;

        public Saver(OutputStream os) {
            this.os = os;
            writer = new BufferedWriter(new OutputStreamWriter(os));
        }

        @Override
        public void save(Sensor.Data data) {
            try {
                if (newline) {
                    writer.write(String.format("%04d%02d%02d%02d%02d%05d", data.year, data.month, data.day, data.hour, data.minute, data.second));
                    newline = false;
                }
                writer.write(' ');
                writer.write(String.valueOf(data.value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void save() {
            try {
                writer.newLine();
                writer.flush();
                newline = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public boolean removing() {
            return removing;
        }
        @Override
        public void close() {
            try {
                writer.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
