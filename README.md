# CardViewPager
自定义ViewPager
##不逼逼，看效果！两边有点露出来的效果，比如腾讯视频App的上方的效果，都是轻量级的控件，请勿见怪，总体时间花费大约9个小时，其中找Bug找了3个小时，哈哈

![微信图片_20170904213432.jpg](http://upload-images.jianshu.io/upload_images/5363507-8ad5b63a07dad73f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 - 第一个效果是正常的滑动情况
![xiao.gif](http://upload-images.jianshu.io/upload_images/5363507-ce7c0fa257533994.gif?imageMogr2/auto-orient/strip)
 - 第二个效果是禁止滑动情况，同时呢，有一个回弹的效果，四川话讲这个很巴适
![xiao.gif](http://upload-images.jianshu.io/upload_images/5363507-fd86e9fec575c1a4.gif?imageMogr2/auto-orient/strip)
##分享两个东西
- 今天发现的一个Android UI 开发效率的 UI 库：https://github.com/QMUI/QMUI_Android
- 这个我都不好意思分享，嘿嘿，周天就做这个，做完了发现根本没有什么东西可以分享，所以就写了现在这个博客，等我以后研究下hexo，才来更新
https://shimingli.github.io/


##写在前面的话：如果我手写慢一点，多看看一下api，我就不会把两个api写错了，由于手滑写错了，导致我这篇博客现在才来写，兴奋感都快磨完了。
![72F75ABC3304DD06A39EB5A18180F6CE.gif](http://upload-images.jianshu.io/upload_images/5363507-dfa1376367aa27eb.gif?imageMogr2/auto-orient/strip)
 - 这辈子我都不会忘记这个值了，getScaledTouchSlop()是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。viewpager就是用这个距离来判断用户是否翻页，只不过呢
```
ViewConfiguration.get(mContext).getScaledTouchSlop()
```
---
原理如下： mTouchSlop = configuration.getScaledPagingTouchSlop();就是这个值，但是你可能会说有毛的的关系啊，别急
![](http://upload-images.jianshu.io/upload_images/5363507-fa2d83618ec0929f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
往ViewConfiguration类看记住这个值
![](http://upload-images.jianshu.io/upload_images/5363507-55e3fe09f76552f2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![image.png](http://upload-images.jianshu.io/upload_images/5363507-3f0eed8d739c16fe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
看这个值mTouchSlop，对吧只不过在ViewPager判断是否需要移动的时候，这个距离是*2。

![image.png](http://upload-images.jianshu.io/upload_images/5363507-df28f20936b2d39c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
由于我这里需要更高的精度，所以获取了这个值getScaledTouchSlop()
---
- 可千万不要拿到getScaledDoubleTapSlop()这个值了啊！
```
//第一次触摸和第二次触摸之间的距离,Distance in pixels between the first touch and second touch
   ViewConfiguration.get(mContext).getScaledDoubleTapSlop();
```
##继承ViewGroup,重写构造方法
```
public class CardViewPager extends ViewGroup{

 public CardViewPager(Context context) {
        this(context,null);
    }

    public CardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
}
```
##初始化init
```
   private void init(Context context) {
        mContext = context;
        //滑动的对象
        mScroller = new Scroller(mContext);
        //getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        // 如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
//        mScaledDoubleTapSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mScaledDoubleTapSlop = ViewConfiguration.get(mContext).getScaledPagingTouchSlop();
        //第一次触摸和第二次触摸之间的距离,Distance in pixels between the first touch and second touch
        ViewConfiguration.get(mContext).getScaledDoubleTapSlop();
        FIRST_width = dp2px(mContext, 10);
        TWO_GAP_WIDTH = FIRST_width * 2;
        THREE_GAP_WIDTH = FIRST_width * 3;
        FOUR_GAP_WIDTH = FIRST_width * 4;
    }
```
##onMeasure,重写测量这里记住widthMeasureSpec、heightMeasureSpec是一个32位的int值，其中高两位是物理模式，低的30位才是控件的宽度和高度的信息。
  MeasureSpec.EXACTLY：父视图希望子视图的大小应该是specSize中指定的。
 MeasureSpec.AT_MOST：子视图的大小最多是specSize中指定的值，也就是说不建议子视图的大小超过specSize中给定的值。
 MeasureSpec.UNSPECIFIED：我们可以随意指定视图的大小。
```
 @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //这才是真正的宽度和高度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heighSize = MeasureSpec.getSize(heightMeasureSpec);
        //设置测量的大小
        setMeasuredDimension(widthSize,heighSize);
        //测量孩子的大小
        mChildCount = getChildCount();
        for (int i=0;i<mChildCount;i++){
            //这里需要把模式也传入进去
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
    }
```
##onLayout重新布局:将孩子的view布局，这里横向的布局，一个字View接着右边,这是设计之初的方法，自己先明白到底是怎么样布局，就好像我明白的方式，是个伟大的ui妹子说，看这个app，就是这样，哈哈
```
/**
     * @param changed
     * @param l 左上角的left
     * @param t top
     * @param r  右下角right
     * @param b bottom值
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child;
        int widthLeft=0;
        for (int i=0;i<mChildCount;i++){
            child = getChildAt(i);
            //得到第一个孩子的宽度，两边都减去了两个参数，记住是这个4倍值
            int measuredWidth = child.getMeasuredWidth() - FOUR_GAP_WIDTH;
            int measuredHeight = child.getMeasuredHeight();
            //是第一个孩子
            if (i==0){
                child.layout(widthLeft+TWO_GAP_WIDTH,0,widthLeft+TWO_GAP_WIDTH+measuredWidth,measuredHeight);
                //改变向左的值
                widthLeft+=measuredWidth+THREE_GAP_WIDTH;
            }else {
                child.layout(widthLeft, 0, widthLeft + measuredWidth, measuredHeight);
                widthLeft += measuredWidth + FIRST_width;
            }
        }
    }
```
####效果虽然看了，但是真正理解的layout的话，还需明白其中的原理，这里我不讲了的太细，献上美图一张，嗦嘎，原理就是，不是每一个屏幕都在装着一个我们的卡片，我们每次移动的时候，也不是移动一个屏幕，而是通过运算的方式，移动到恰好能够看到两边10dp的值，这里的要转成像素
```
 dp2px(mContext, 10);
```
![image.png](http://upload-images.jianshu.io/upload_images/5363507-af8c440ead2eccfe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
关于像素px我还想说说: context.getResources().getDisplayMetrics().density;density显示器的逻辑密度，这是【独立的像素密度单位（首先明白dp是个单位）】的一个缩放因子，在屏幕密度大约为160dpi的屏幕上，一个dp等于一个px,这个提供了系统显示器的一个基线. 例如：屏幕为240*320的手机屏幕，其尺寸为 1.5"*2"  也就是1.5英寸乘2英寸的屏幕 它的dpi（屏幕像素密度，也就是每英寸的像素数，dpi是dot per inch的缩写）大约就为160dpi， 所以在这个手机上dp和px的长度（可以说是长度，最起码从你的视觉感官上来说是这样的）是相等的。 因此在一个屏幕密度为160dpi的手机屏幕上density的值为1，而在120dpi的手机上为0.75等等.例如：一个240*320的屏幕尽管他的屏幕尺寸为1.8"*1.3",（我算了下这个的dpi大约为180dpi多点）但是它的density还是1(也就是说取了近似值) 然而，如果屏幕分辨率增加到320*480 但是屏幕尺寸仍然保持1.5"*2" 的时候（和最开始的例子比较）
 这个手机的density将会增加（可能会增加到1.5）
```
 public static int dp2px(Context context, float dpValue) {
        ///这个得到的不应该叫做密度，应该是密度的一个比例。不是真实的屏幕密度，
        /// 而是相对于某个值的屏幕密度。也可以说是相对密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
```
##拦截事件:当大于了需要移动控件的距离的话，就需要把这个事件拦截自己处理。
```
 @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                mLastMotionX = x ;
                break;
            case MotionEvent.ACTION_MOVE:
                x= (int) ev.getX();
                //滑动的距离
                int delX = mLastMotionX - x;
                //如果说距离大于这个距离的话，就需要滚动了，拦截事件
                if (Math.abs(delX)>mScaledDoubleTapSlop){
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

```
##处理事件:在down的事件一定需要拦截，才能记录坐标
  返回值为True，代表拦截这次事件，直接进入到ViewGroup的onTouchEvent中，就不会进入到View的onTouchEvent了
  返回值为False，代表不拦截这次事件，不进入到ViewGroup的onTouchEvent中，直接进入到View的onTouchEvent中
```
 public boolean onTouchEvent(MotionEvent event) {
        //如果没有孩子的话，不需要拦截
        if (getChildCount()==0){
            return false;
        }
        //监听滑动的速度
        obtainTracker(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    //停止动画
                    mScroller.abortAnimation();
                }
                int x = (int) event.getX();
                mLastMotionX=x;
                //不管怎么怎么样这个事件都必须拦截
                return true;

            case MotionEvent.ACTION_MOVE:
                 x = (int) event.getX();
                int desX = mLastMotionX - x;
                //这个距离大于了屏幕的10/1的话，就给他赋值10/1
                if (!isAllowScroll&&desX>getWidth()/10){
                    desX=getWidth()/10;
                    //如果设置了不可以滑动的，这个flag需要到up事件单独处理
                    mCanScrolled = true;
                }
                //只需计算x的距离
                mVelocityTracker.computeCurrentVelocity(1000,ViewConfiguration.getMaximumFlingVelocity());
                mXVelocity = (int) mVelocityTracker.getXVelocity();
                //如果说距离滑动太小，或者是只有一个屏幕的话，就不往下去做操作了
                if (Math.abs(desX)<mScaledDoubleTapSlop||(desX>=0&&
                        mCurScreen==mChildCount-1)||(desX<=0&&mCurScreen==0)){
                    break;
                }

                //能到这里来的话，就必须往手指方向慢慢滚动了
                scrollTo(getChildAt(mCurScreen).getLeft()+desX,0);
                break;
            case MotionEvent.ACTION_UP:
                //mXVelocity为正数的话，这个是往left滚
                if (isAllowScroll&&mXVelocity>MAX_VELOCITY_VALUE&&mCurScreen>0){
                    scrollScreen(mCurScreen-1);
                }else if (isAllowScroll&&mXVelocity<-MAX_VELOCITY_VALUE&&mCurScreen<mChildCount-1){
                    scrollScreen(mCurScreen+1);
               //当设置了不能滑动时候，并且手指滑动的Move的距离已经超过了屏幕的10/1，有一个回弹的效果，左右摇摆
                }else if (mCanScrolled){
                    springToDestination();
                }else{
                    snapToDestination();
                }
                //最后不要忘记了释放
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }
```
##滑动到指定的屏幕,在记住两个地方，就不需要滑动了，一个是在最右和最左端。
```
 private void scrollScreen(int whichScreen) {
        //防止超出了最大的孩子的数量
        int min = Math.min(whichScreen, mChildCount - 1);
        whichScreen = Math.max(0, min);
        //getScrollX() 就是当前view的左上角相对于母视图的左上角的X轴偏移量。
        //在这里当getScrollX==0的时候，等于后面的whichScreen*getWidth()那么就滑动到第一页了
        //后续就不需要滑动了，也不需要重新绘制了
        // TODO: 2017/9/3 这里只在最左不能进去滑动了，其实在最右端也是不能够去滑动了，带解决
        if (getScrollX()!=whichScreen*getWidth()){
            int deltaX = whichScreen * (getWidth() - THREE_GAP_WIDTH) - getScrollX();
            mCurScreen = whichScreen;
            mScroller.startScroll(getScrollX(), 0, deltaX, 0, Math.abs(deltaX));
            invalidate();
        }
    }
```
但是在这里我留下一个问题，但是在这里我留下一个问题在我的手机上我测试了1到5个孩子的情况分别数据如下：
getScrollX()和whichScreen*getWidth()
     1个屏幕是0  0--------0
     2个屏幕是90 990  -------1080
     3个屏是 120
     4个屏  =270 2970  -------3240
     5个屏  =380  3960  -------4320
     当我们滑动到最有端的时候，其实也是不能够去滑动了，但是我这个方法呢是能够 走到if当中的，而且对应关系也不太明确，这个问题我还得想想。
##还需要理解一个东西getScrollX()到底是什么值？再次献上我的美作，哈哈，反正我是明白了，我怕讲不明白，所以先看图，然后抓日记，一下就明白!

![image.png](http://upload-images.jianshu.io/upload_images/5363507-53e2ed2e6329a2b2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##监听滑动的速度，在上篇笔锋效果里面有讲到过，还是Viewpager里面的东西,记住要释放这个算是监听吧！
```
     //监听滑动的速度
        obtainTracker(event);

    private void obtainTracker(MotionEvent event) {
        if (mVelocityTracker==null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        //绑定事件
        mVelocityTracker.addMovement(event);
    }
  /**
     * 释放监听滑动速度方法
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
```
##其实你就把上面的工作全部都做好了，你会发现还是不能够翻页，来吧去Viewpager看看，再去度娘看看
```
  /**
     * 计算滚动的位置
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        //返回值为boolean，mScroller.computeScrollOffset()==true说明滚动尚未完成，false说明滚动已经完成。
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }

    }

```

![image.png](http://upload-images.jianshu.io/upload_images/5363507-b65df02b20499072.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
个人翻译就是在viewpager中需要重新计算滑动的位置
![image.png](http://upload-images.jianshu.io/upload_images/5363507-f9b51f5e11529f03.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##回弹滑动目的屏,这里就是需要有点动画的效果，先回到原来的位置，然后左右摇摆摇摆！
```
/**
     * 回弹滑动目的屏
     */
    private void springToDestination() {
        System.out.println("shiming  ==springToDestination");
        int screenWidth = getWidth();
        int whichScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        whichScreen = Math.max(0, Math.min(whichScreen, mChildCount - 1));
        final int deltaX = whichScreen * (getWidth() - THREE_GAP_WIDTH) - getScrollX();
        mCurScreen = whichScreen;
        //先给我滚动到原来的位置
        springToScroll(deltaX * 1.0f, Math.abs(deltaX));
        //向右的给我摆动两下
        postDelayed(new Runnable() {
            @Override
            public void run() {
                springToScroll(-deltaX * 0.3f, Math.abs(deltaX));
            }
        }, Math.abs(deltaX));
        //让后给我向左摆动两下
        postDelayed(new Runnable() {
            @Override
            public void run() {
                springToScroll(deltaX * 0.3f, Math.abs(deltaX));
            }
        }, Math.abs(deltaX * 2));
        mCanScrolled = false;
    }

 /**
     *  getScrollX() 水平方向滚动的偏移值，以像素为单位。正值表明滚动将向左滚动
     　　startY 垂直方向滚动的偏移值，以像素为单位。正值表明滚动将向上滚动
     　　(int) deltaX 水平方向滑动的距离，正值会使滚动向左滚动
     　 0  垂直方向滑动的距离，正值会使滚动向上滚动
     * @param deltaX
     * @param duration
     */
    private void springToScroll(float deltaX, int duration) {
        mScroller.startScroll(getScrollX(), 0, (int) deltaX, 0, duration);
        invalidate();
    }
```

##好了，以上，我写的写的都要睡早了，代码有些注释还比较详细一点，如有需要看代码吧，由于工程逻辑上，还有很多复杂的代码，我这里就提取了一部分，以供学习之用，谢谢！
地址Git：https://github.com/Shimingli/CardViewPager