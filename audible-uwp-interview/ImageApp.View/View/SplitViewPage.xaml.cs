using ImageApp.Model;
using ImageApp.ViewModel;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;
using static ImageApp.Model.ImageGalleryManager;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace ImageApp.View
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class SplitViewPage : Page
    { 
        public SplitViewPage()
        {
            this.InitializeComponent();
            NavigationCacheMode = NavigationCacheMode.Required;
            InstagramButton.IsChecked = true;
        }

        private void OnMenuButtonClicked(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = !ShellSplitView.IsPaneOpen;
            ((RadioButton)sender).IsChecked = false;
        }

        private void OnContentMenuButtonClicked(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = !ShellSplitView.IsPaneOpen;
            ((RadioButton)sender).IsChecked = false;
        }

        private void OnInstagramButtonChecked(object sender, RoutedEventArgs e)
        {
            GalleryViewModel.Instance().ActiveService = ImageService.Instagram;
            ShellSplitView.IsPaneOpen = false;
            ((Frame)ShellSplitView.Content).Navigate(typeof(GalleryViewPage), this);
        }

        private void OnFlickrButtonChecked(object sender, RoutedEventArgs e)
        {
            GalleryViewModel.Instance().ActiveService = ImageService.Flickr;
            ShellSplitView.IsPaneOpen = false;
            ((Frame)ShellSplitView.Content).Navigate(typeof(GalleryViewPage), this);
        }

        private void OnGiphyButtonChecked(object sender, RoutedEventArgs e)
        {
            GalleryViewModel.Instance().ActiveService = ImageService.Giphy;
            ShellSplitView.IsPaneOpen = false;
            ((Frame)ShellSplitView.Content).Navigate(typeof(GalleryViewPage), this);
        }

        private void OnSettingsButtonChecked(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
        }
    }
}
