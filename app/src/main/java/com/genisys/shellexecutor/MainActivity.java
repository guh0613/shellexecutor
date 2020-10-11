package com.genisys.shellexecutor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final CheckBox ifroot = findViewById(R.id.asroot);
        final EditText commandline = findViewById(R.id.input);
        final TextView output = findViewById(R.id.outputtext);
        FloatingActionButton fab = findViewById(R.id.fab);

        //总之先看看有没有读写权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("权限授予");
            dialog.setMessage("尊敬的用户，您好，鉴于您尚未授予我们权限，我们在此斗胆向您提出请求。");
            dialog.setCancelable(false);
            dialog.setPositiveButton("是是是，我马上照办", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog,int which)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new
                            String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            });
            dialog.setNegativeButton("这权限，不给也罢", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog,int which)
                {
                    Toast.makeText(MainActivity.this,"(눈_눈)",Toast.LENGTH_SHORT).show();


                }


            });
            dialog.show();
        }

        //想以root身份运行？
        ifroot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ShellUtils.checkRootPermission() == false && isChecked == true) {
                    android.app.AlertDialog.Builder seemnoroot =new android.app.AlertDialog.Builder(MainActivity.this);
                    seemnoroot.setTitle("关于root权限");
                    seemnoroot.setMessage("您似乎并未授予我们root权限，或者请检查贵机是非已root。");
                    seemnoroot.setPositiveButton("已阅", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShellUtils.checkRootPermission();
                        }
                    });
                    seemnoroot.show();
                    ifroot.setChecked(false);
            }
            }
        });


        //运行命令
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = commandline.getText().toString();
                if (input.length()==0) {
                    //善意提醒这个B啥也没写
                    commandline.setError("要是你啥也没写，我也没办法帮你执行");
                }
                else {
                    Snackbar.make(view, "哦哦......！全身都燃烧起来了！开始执行！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Boolean shouldasroot = ShellUtils.checkRootPermission() && ifroot.isChecked();
                    ShellUtils.CommandResult result= ShellUtils.execCommand(input,shouldasroot);
                    output.setText(result.successMsg+result.errorMsg);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}