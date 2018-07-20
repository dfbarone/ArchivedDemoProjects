using ImageApp.Common;
using ImageApp.Model;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace ImageApp.ViewModel
{
    public class GalleryViewModel : BindableBase
    {
        public ImageCreatorInterface _imageCreator = null;
        private ImageGalleryManager _imageGalleryManager = null;
        public ObservableCollection<ImageDataItem> ImageCollection { get; private set; }

        public ImageService ActiveService
        {
            get
            {
                return _imageGalleryManager.ActiveService;
            }
            set
            {
                _imageGalleryManager.ActiveService = value;
            }
        }

        public string GetSearchString()
        {
            return _imageGalleryManager.SearchString;
        }

        public async Task SetSearchString(string search)
        {
             if (!string.IsNullOrEmpty(search))
             {
                _imageGalleryManager.SearchString = search;
                await GetFirstImages();
            }
        }
        
        private static GalleryViewModel _instance = null;

        public static GalleryViewModel Instance(ImageCreatorInterface bitmapImageCreator = null)
        {
            if (_instance == null)
                _instance = new GalleryViewModel();

            if (_instance._imageCreator == null && bitmapImageCreator != null)
                _instance._imageCreator = bitmapImageCreator;

            return _instance;
        }

        private GalleryViewModel()
        {
            _imageGalleryManager = new ImageGalleryManager();
            ImageCollection = new ObservableCollection<ImageDataItem>();
            _imageGalleryManager.SearchString = "selfie";
        }

        private async Task GetImages(string searchString, bool pagination)
        {
            List<ImageItem> items = await _imageGalleryManager.GetImages(searchString, pagination);

            if (items.Count == 0)
                return;

            List<ImageDataItem> imageList = new List<ImageDataItem>();
            List<Task<bool>> taskList = new List<Task<bool>>();

            int index = ImageCollection.Count;
            for (int i = 0; i < items.Count; i++)
            {
                ImageDataItem dataItem = new ImageDataItem(index++, items[i].Title, items[i].Url, ActiveService, _imageCreator);
                imageList.Add(dataItem);
                Task<bool> task = dataItem.Init();
            }

            await Task.WhenAll(taskList.ToArray());
                
            if (!pagination)
                ImageCollection.Clear();

            foreach (var dataItem in imageList)
            {
                ImageCollection.Add(dataItem);
            }
        }

        public async Task GetFirstImages()
        {
            await GetImages(GetSearchString(), false);
        }

        public async Task GetNextImages()
        {
            await GetImages(GetSearchString(), true);
        }
    }
}
