using System.Collections.Generic;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    public enum ImageService
    {
        Instagram,
        Flickr,
        Giphy
    }

    public class ImageGalleryManager
    {
        private WebServiceManager _webServiceManager = new WebServiceManager();

        // The active service, currently Instagram or Flickr
        public ImageService ActiveService { get; set; }

        public string SearchString { get; set; }

        private bool _loading = false;
        public string _currentPage = "";

        /// <summary>
        /// 
        /// </summary>
        /// <param name="searchString"></param>
        ///     search string tag
        /// <param name="pagination"></param>
        ///     true: pagination assumes loading the next page of images
        ///     false: this will only load the first page
        /// <returns></returns>
        public async Task<List<ImageItem>> GetImages(string searchString, bool pagination)
        {
            List<ImageItem> items = new List<ImageItem>();

            if (_loading)
                return items;

            if (string.IsNullOrEmpty(SearchString))
                return items;

            _loading = true;

            SearchString = searchString;
            if (ActiveService == ImageService.Instagram)
            {
                if (!pagination)
                    _currentPage = "";

                items = await _webServiceManager.Load<ImageItem>(new InstagramLoader(searchString, _currentPage));

                // Save next page id expecting pagination will be used
                // this is oddly not a number but a long string so we cant increment.
                _currentPage = (items.Count > 0) ? items[0].NextPageId : "";
            }
            else if (ActiveService == ImageService.Flickr)
            {
                if (!pagination)
                    _currentPage = "1";

                items = await _webServiceManager.Load(new FlickrLoader(searchString, _currentPage));

                // Increments page id expecting pagination will be used
                // this is a number so we can increment
                _currentPage = (int.Parse(_currentPage) + 1).ToString();
            }
            else
            {
                if (!pagination)
                    _currentPage = "0";

                items = await _webServiceManager.Load(new GiphyLoader(searchString, _currentPage));

                // Save next page id expecting pagination will be used
                _currentPage = (items.Count > 0) ? items[0].NextPageId : "";
            }

            _loading = false;
            return items;
        }
    }
}
