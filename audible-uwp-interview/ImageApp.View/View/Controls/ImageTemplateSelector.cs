using ImageApp.Model;
using ImageApp.ViewModel;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace ImageApp.View
{
    class ImageTemplateSelector : DataTemplateSelector
    {
        public DataTemplate ImgTemplate { get; set; }

        public DataTemplate GifTemplate { get; set; }

        protected override DataTemplate SelectTemplateCore(object item, DependencyObject container)
        {
            var currentFrame = Window.Current.Content as Frame;
            var currentPage = currentFrame.Content as Page;

            FrameworkElement element = container as FrameworkElement;

            if (element != null && item != null && item is ImageDataItem)
            {
                ImageDataItem imageItem = item as ImageDataItem;
                if (imageItem.ImageType == ImageService.Giphy)
                {
                    return GifTemplate;
                }
                else
                {
                    return ImgTemplate;
                }
            }
            return null;
        }
    }
}
