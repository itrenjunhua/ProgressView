# Android 进度控件

## 实现效果如图

![Progress](https://github.com/itrenjunhua/ProgressView/raw/master/images/progress.gif) 
![Touch](https://github.com/itrenjunhua/ProgressView/raw/master/images/touch.gif)

## 控件的使用

* ### 布局中设置属性

		<!--  CircleProgressView（圆形进度条） -->
		<com.renj.progress.CircleProgressView
	        android:id="@+id/circle_pv_view"
	        android:layout_width="wrap_content"
	        android:layout_height="200dp"
	        android:layout_marginTop="10dp"
	        app:circle_pv_current="20"
	        app:circle_pv_show_type="decimal"
	        app:circle_pv_start_point="top_left"
	        app:circle_pv_total="100" />

		<!--  SemicircleProgressView（半圆形进度条） -->
	    <com.renj.progress.SemicircleProgressView
	        android:id="@+id/semicircle_pv_view"
	        android:layout_width="wrap_content"
	        android:layout_height="200dp"
	        android:layout_marginTop="40dp"
	        app:semicircle_pv_current="50"
	        app:semicircle_pv_total="100" />

		<!-- ScaleView（刻度尺效果） -->
		<com.renj.progress.ScaleView
	        android:id="@+id/scale_view"
	        android:layout_width="match_parent"
	        android:layout_height="80dp"
	        android:visibility="visible"
	        app:scale_current_color="@color/colorAccent"
	        app:scale_default_value="160"
	        app:scale_end_value="250"
	        app:scale_start_value="60"
	        app:scale_step_length_value="10"
	        app:scale_unit="cm" />

		<!--  SemicircleSeekBar（半圆拖动控件） -->
	    <com.renj.progress.SemicircleSeekBar
	        android:id="@+id/semicircle_sb_view"
	        android:layout_width="wrap_content"
	        android:layout_height="200dp"
	        android:layout_marginTop="40dp"
	        app:semicircle_sb_color="@color/colorPrimary"
	        app:semicircle_sb_current="50"
	        app:semicircle_sb_thumb_bitmap="@mipmap/ic_launcher"
	        app:semicircle_sb_thumb_color="@color/colorAccent"
	        app:semicircle_sb_thumb_size="12dp"
	        app:semicircle_sb_total="100"
	        app:semicircle_sb_width="5dp" />


* ### 代码中设置值

		// CircleProgressView（圆形进度条）
		circleProgressView.setValue(100, 60).takeEffect();

		// SemicircleProgressView（半圆形进度条）
        semicircleProgressView.setValue(100, 70).takeEffect();

		// ScaleView（刻度尺效果）
		scaleView.setValue(10, 180, 10, 45, "kg").takeEffect();

		// SemicircleSeekBar（半圆拖动控件）
        semicircleSeekBar.setValue(100, 60).takeEffect();


* ### 设置数据改变监听

		// CircleProgressView（圆形进度条）
		circleProgressView.setOnProgressChangeListener((circleProgressView, currentValue) ->
                Log.i("ProgressActivity", "currentValue = [" + currentValue + "]"));
		
		// SemicircleProgressView（半圆形进度条）
        semicircleProgressView.setOnProgressChangeListener((semicircleProgressView, currentValue) ->
                Log.i("ProgressActivity", "currentValue = [" + currentValue + "]"));
		
		// ScaleView（刻度尺效果）
		scaleView.setOnScaleChangeListener((scaleView, currentValue, unit, startValue, endValue, stepLengthValue) ->
                Log.i("TouchActivity", "currentValue = [" + currentValue + "]," +
                        " unit = [" + unit + "], " + "startValue = [" + startValue + "], " +
                        "endValue = [" + endValue + "], " + "stepLengthValue = [" + stepLengthValue + "]"));
		
		// SemicircleSeekBar（半圆拖动控件）
        semicircleSeekBar.setOnProgressChangeListener((semicircleProgressView, currentValue) ->
                Log.i("TouchActivity", "currentValue = [" + currentValue + "]"));

## 各个控件属性

* ### CircleProgressView（圆形进度条） 控件属性


    	 <declare-styleable name="CircleProgressView">
            <!--背景圆圈颜色-->
            <attr name="circle_pv_bg_color" format="color" />
            <!--进度颜色-->
            <attr name="circle_pv_color" format="color" />
            <!--进度条宽度-->
            <attr name="circle_pv_width" format="dimension" />
            <!--文字颜色-->
            <attr name="circle_pv_text_color" format="color" />
            <!--文字大小-->
            <attr name="circle_pv_text_size" format="dimension" />
            <!--当前结果显示样式-->
            <attr name="circle_pv_show_type" format="enum">
                <!--不显示当前值-->
                <enum name="none" value="0" />
                <!--小数点形式显示-->
                <enum name="decimal" value="1" />
                <!--百分比形式显示，默认-->
                <enum name="percentage" value="2" />
            </attr>
            <!--进度开始位置控制-->
            <attr name="circle_pv_start_point" format="enum">
                <!--上方-->
                <enum name="top" value="0" />
                <!--右上方-->
                <enum name="top_right" value="1" />
                <!--右方-->
                <enum name="right" value="2" />
                <!--右下方-->
                <enum name="bottom_right" value="3" />
                <!--下方-->
                <enum name="bottom" value="4" />
                <!--左下方-->
                <enum name="bottom_left" value="5" />
                <!--左方-->
                <enum name="left" value="6" />
                <!--左上方-->
                <enum name="top_left" value="7" />
            </attr>

            <!--进度总大小-->
            <attr name="circle_pv_total" format="integer" />
            <!--当前进度-->
            <attr name="circle_pv_current" format="integer" />
        </declare-styleable>


* ### SemicircleProgressView（半圆形进度条） 控件属性

	    <declare-styleable name="SemicircleProgressView">
	        <!--背景圆圈颜色-->
	        <attr name="semicircle_pv_bg_color" format="color" />
	        <!--进度颜色-->
	        <attr name="semicircle_pv_color" format="color" />
	        <!--进度条宽度-->
	        <attr name="semicircle_pv_width" format="dimension" />
	        <!--文字颜色-->
	        <attr name="semicircle_pv_text_color" format="color" />
	        <!--文字大小-->
	        <attr name="semicircle_pv_text_size" format="dimension" />
	        <!--当前进度文字颜色-->
	        <attr name="semicircle_pv_current_text_color" format="color" />
	        <!--当前进度文字颜色-->
	        <attr name="semicircle_pv_current_text_size" format="dimension" />
	        <!--当前结果显示样式-->
	        <attr name="semicircle_pv_show_type" format="enum">
	            <!--不显示当前值-->
	            <enum name="none" value="0" />
	            <!--小数点形式显示-->
	            <enum name="decimal" value="1" />
	            <!--百分比形式显示，默认-->
	            <enum name="percentage" value="2" />
	        </attr>
	
	        <!--进度总大小-->
	        <attr name="semicircle_pv_total" format="integer" />
	        <!--当前进度-->
	        <attr name="semicircle_pv_current" format="integer" />
	
	    </declare-styleable>


* ### ScaleView（刻度尺效果） 控件属性

	    <declare-styleable name="ScaleView">
	        <!--线颜色-->
	        <attr name="scale_line_color" format="color" />
	        <!--主线宽度-->
	        <attr name="scale_main_line_width" format="dimension" />
	        <!--默认当前位置刻度线宽度-->
	        <attr name="scale_current_line_width" format="dimension" />
	        <!--刻度线宽度-->
	        <attr name="scale_line_width" format="dimension" />
	        <!--长刻度线高度-->
	        <attr name="scale_line_height_long" format="dimension" />
	        <!--短刻度线高度-->
	        <attr name="scale_line_height_short" format="dimension" />
	        <!--每一个刻度单元格宽度(两个长刻度之间的宽度)-->
	        <attr name="scale_cell_width" format="dimension" />
	        <!--文字和刻度线之间的距离-->
	        <attr name="scale_text_space" format="dimension" />
	        <!--刻度文字颜色-->
	        <attr name="scale_text_color" format="color" />
	        <!--刻度文字大小-->
	        <attr name="scale_text_size" format="dimension" />
	        <!--是否显示/绘制当前值信息-->
	        <attr name="scale_is_show_current_info" format="boolean" />
	        <!--显示当前刻度文字大小-->
	        <attr name="scale_current_text_size" format="dimension" />
	        <!--当前刻度和文字颜色-->
	        <attr name="scale_current_color" format="color" />
	
	        <!--单位/显示当前值时的单位-->
	        <attr name="scale_unit" format="string" />
	        <!--开始值-->
	        <attr name="scale_start_value" format="integer" />
	        <!--结束值-->
	        <attr name="scale_end_value" format="integer" />
	        <!--步长(每两个刻度之间的差值)，注意：步长(stepLengthValue)必须为大于0的整数.-->
	        <attr name="scale_step_length_value" format="integer" />
	        <!--默认值，注意：范围应该在 startValue 和 endValue 之间-->
	        <attr name="scale_default_value" format="integer" />
	    </declare-styleable>



* ### SemicircleSeekBar（半圆拖动控件） 控件属性

	    <declare-styleable name="SemicircleSeekBar">
	        <!--背景圆圈颜色-->
	        <attr name="semicircle_sb_bg_color" format="color" />
	        <!--进度颜色-->
	        <attr name="semicircle_sb_color" format="color" />
	        <!--进度条宽度-->
	        <attr name="semicircle_sb_width" format="dimension" />
	        <!--滑动图标半径-->
	        <attr name="semicircle_sb_thumb_radius" format="dimension" />
	        <!--滑动图标颜色-->
	        <attr name="semicircle_sb_thumb_color" format="color" />
	        <!--滑动图标-->
	        <attr name="semicircle_sb_thumb_bitmap" format="reference" />
	        <!--滑动图标大小-->
	        <attr name="semicircle_sb_thumb_size" format="dimension" />
	        <!--环形四周间距,有文字时修改文字与环形的对齐方式-->
	        <attr name="semicircle_sb_innerMargin" format="dimension" />
	        <!--最大值/最小值文字与环形的距离值-->
	        <attr name="semicircle_sb_textPadding" format="dimension" />
	        <!--文字颜色-->
	        <attr name="semicircle_sb_text_color" format="color" />
	        <!--文字大小-->
	        <attr name="semicircle_sb_text_size" format="dimension" />
	        <!--当前进度文字颜色-->
	        <attr name="semicircle_sb_current_text_color" format="color" />
	        <!--当前进度文字颜色-->
	        <attr name="semicircle_sb_current_text_size" format="dimension" />
	        <!--当前结果显示样式-->
	        <attr name="semicircle_sb_show_type" format="enum">
	            <!--不显示当前值-->
	            <enum name="none" value="0" />
	            <!--小数点形式显示-->
	            <enum name="decimal" value="1" />
	            <!--百分比形式显示，默认-->
	            <enum name="percentage" value="2" />
	        </attr>
	
	        <!--进度总大小-->
	        <attr name="semicircle_sb_total" format="integer" />
	        <!--当前进度-->
	        <attr name="semicircle_sb_current" format="integer" />
	
	    </declare-styleable>



