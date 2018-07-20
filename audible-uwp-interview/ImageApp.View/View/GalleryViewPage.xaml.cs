using ImageApp.Model;
using ImageApp.ViewModel;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace ImageApp.View
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class GalleryViewPage : Page
    {
        private GalleryViewModel _galleryViewModel;
        private SplitViewPage _parent;
        private bool _isNewPageInstance = true;
        private ImageService _activeService;

        public GalleryViewPage()
        {
            this.InitializeComponent();

            NavigationCacheMode = NavigationCacheMode.Required;
        }

        override
        protected async void OnNavigatedTo(NavigationEventArgs e)
        {
            if (e.Parameter != null && e.Parameter is SplitViewPage)
            {
                _parent = (SplitViewPage)e.Parameter;
            }

            if (_isNewPageInstance)
            {
                _galleryViewModel = GalleryViewModel.Instance();

                _activeService = _galleryViewModel.ActiveService;
                SearchTextBox.Text = _galleryViewModel.GetSearchString();

                GalleryViewSource.Source = _galleryViewModel.ImageCollection;
                await _galleryViewModel.GetFirstImages();
            }

            // re sync images when we switch between services
            if (_activeService != _galleryViewModel.ActiveService)
            {
                _activeService = _galleryViewModel.ActiveService;

                GalleryScrollViewer.ChangeView(null, 0, null);
                await _galleryViewModel.GetFirstImages();
            }

            // set title
            if (_galleryViewModel.ActiveService == ImageService.Instagram)
                TitleText.Text = "Instagram";
            else if (_galleryViewModel.ActiveService == ImageService.Flickr)
                TitleText.Text = "Flickr";
            else
                TitleText.Text = "Giphy"; 

            _isNewPageInstance = false;
        }

        private void GalleryGridView_ItemClick(object sender, ItemClickEventArgs e)
        {
            ImageDataItem i = (ImageDataItem)e.ClickedItem;
            if (i.ImageType == ImageService.Giphy)
            {
                i.Play = !i.Play;
                i.ImagePath = i.ImagePath;
            }
            else
            {
                if (_parent != null)
                    _parent.Frame.Navigate(typeof(ImageViewPage), i);
            }
        }

        private async void RefreshButton_Click(object sender, RoutedEventArgs e)
        {
            GalleryScrollViewer.ChangeView(null, 0, null);
            await _galleryViewModel.GetFirstImages();
        }

        private async void SearchTextBox_KeyDown(object sender, KeyRoutedEventArgs e)
        {
            if (e.Key == Windows.System.VirtualKey.Enter)
            {
                GalleryScrollViewer.ChangeView(null, 0, null);
                await _galleryViewModel.SetSearchString(SearchTextBox.Text);
            }
        }

        private async void ScrollViewer_ViewChanged(object sender, ScrollViewerViewChangedEventArgs e)
        {
            var scrollViewer = (ScrollViewer)sender;
            if (scrollViewer.VerticalOffset > scrollViewer.ScrollableHeight - 300)
            {
                await _galleryViewModel.GetNextImages();
            }
        }
    }
}
