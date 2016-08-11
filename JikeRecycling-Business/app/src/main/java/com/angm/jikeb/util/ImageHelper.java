package com.angm.jikeb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.angm.jikeb.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * 图片缓存帮助类
 * 
 * 使用方式：ImageHelper.getInstance(context).loadImage(imageView, url);
 * @author ThinkPad
 *
 */
public class ImageHelper {
	
	private final static String CACHE_DIR = "EasyCoding/Cache"; //缓存路径
	private final static int DEFAULT_IMAGE_RESOURCE = R.mipmap.ic_launcher; //默认图片资源
	private final static int IMAGE_MAX_WIDHT = 128; //缓存图片最大宽度
	private final static int IMAGE_MAX_HEIGHT = 128; //缓存图片最大高度
	private final static int CORNER_RADIUS_PIXELS = 20; //圆角像素
	private final static int MEMORY_CACHE = 5 * 1024 * 1024; //内存缓存空间
	private final static int MEMORY_MAX_CACHE = 5 * 1024 * 1024; //内存缓存最大空间
	private final static int CONNECT_TIME_OUT = 5 * 1000; //连接超时时间
	private final static int READ_TIME_OUT =  30 * 1000; //读取超时时间

	private static ImageHelper helper;
	
	private static File cacheFile ;//缓存文件路径
	private static ImageLoadingListener animateFirstListener;//监听
	private static ImageLoaderConfiguration imageLoaderConfig;//配置
	private static ImageLoader imageLoader = ImageLoader.getInstance();//实例
	private static DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	
	@SuppressWarnings("deprecation")
	public static ImageHelper getInstance(Context context){
		if(helper == null){
			helper = new ImageHelper();
			animateFirstListener  = new SimpleImageLoadingListener();
			cacheFile = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);//设置缓存路径
			imageLoaderConfig = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(IMAGE_MAX_WIDHT, IMAGE_MAX_HEIGHT)// 保存每个缓存图片的最大长和宽
			            .threadPoolSize(3)// 线程池的大小 这个其实默认就是3
			            .threadPriority(Thread.NORM_PRIORITY - 2)//降低线程的优先级保证主UI线程不受太大影响
			            .discCache(new UnlimitedDiscCache(cacheFile))
			            .memoryCache(new UsingFreqLimitedMemoryCache(MEMORY_CACHE)) //建议内存设在5-10M,可以有比较好的表现
			            .memoryCacheSize(MEMORY_MAX_CACHE) 
			            //.discCacheSize(50 * 1024 * 1024) 
			            .denyCacheImageMultipleSizesInMemory()// 缓存显示不同大小的同一张图片
			            //.discCacheFileCount(100) //缓存的文件数量 
			            //.discCacheFileNameGenerator(new Md5FileNameGenerator()) 
			            //.tasksProcessingOrder(QueueProcessingType.LIFO) 
			            .imageDownloader(new BaseImageDownloader(context, CONNECT_TIME_OUT, READ_TIME_OUT)) // connectTimeout超时时间
			            .build();
			options = new DisplayImageOptions.Builder()
					//.showStubImage(R.drawable.default_img) // 设置图片下载期间显示的图片
					.showImageForEmptyUri(DEFAULT_IMAGE_RESOURCE) // 设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(DEFAULT_IMAGE_RESOURCE) // 设置图片加载或解码过程中发生错误显示的图片
					.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
					.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.RGB_565)
//					.displayer(new FadeInBitmapDisplayer(50))// 图片加载好后渐入的动画时间
					.delayBeforeLoading(50)//
					.displayer(new RoundedBitmapDisplayer(CORNER_RADIUS_PIXELS)) // 设置成圆角图片
					.build(); // 创建配置过得DisplayImageOption对象
			imageLoader.init(imageLoaderConfig);
		}
		return helper;
	}
	
	/**
	 * 装载图片
	 * 
	 * @param imageView
	 * @param url
	 * @param defaultImageResource
	 */
	public void loadImage(ImageView imageView, String url){
		imageLoader.cancelDisplayTask(imageView);
		imageLoader.displayImage(url,new ImageViewAware(imageView,false), options,animateFirstListener);
	}
	
	/**
	 * 清理掉缓存图片
	 */
	@SuppressWarnings("deprecation")
	public static void clearCatchImage(){
        imageLoader.clearMemoryCache();
        imageLoader.clearDiscCache();
        AnimateFirstDisplayListener.displayedImages.clear();
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		 
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
	 
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 50);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
}
