using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    class FlickrLoader : Loader<ImageItem>
    {
        public FlickrLoader(string search, string pagination)
        {
            string url = string.Format("https://api.flickr.com/services/rest/?method={0}&tags={1}&format=json&api_key={2}&page={3}&per_page=50",
                "flickr.photos.search",
                Uri.EscapeDataString(search),
                "6efe02cf7de7961bae19f9bb965d302a",
                pagination);

            Url = url;
        }

        override
        public async Task<List<ImageItem>> Load()
        {
            List<ImageItem> imageItems = new List<ImageItem>();

            try
            {
                string content = await GetAsync(Url);
                content = content.TrimOuter("{", "}");

                JObject jsonObj = JObject.Parse(content);
                JObject photosObj = (JObject)jsonObj["photos"];
                JArray photoArray = (JArray)photosObj["photo"];

                foreach (JObject imageObj in photoArray)
                {
                    var item = new FlickrItem(imageObj);
                    item.NextPageId = Pagination;
                    imageItems.Add(item);
                }
                return imageItems;
            }
            catch (Exception) { }

            return imageItems;
        }
    }
}
