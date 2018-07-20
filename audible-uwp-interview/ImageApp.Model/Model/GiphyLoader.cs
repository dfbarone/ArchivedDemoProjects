using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    class GiphyLoader : Loader<ImageItem>
    {
        public GiphyLoader(string search, string pagination)
        {
            search = search.Replace(" ", "+");
            string url = string.Format("http://api.giphy.com/v1/gifs/search?api_key={0}&q={1}&offset={2}",
                "dc6zaTOxFJmzC",
                Uri.EscapeDataString(search),
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
                JObject jsonObj = JObject.Parse(content);

                // get pagination data
                JObject paginationObj = (JObject)jsonObj["pagination"];
                string count = (string)paginationObj["count"];
                string offset = (string)paginationObj["offset"];
                string nextPageId = (int.Parse(offset) + int.Parse(count)).ToString();

                // get image data
                JArray imageArray = (JArray)jsonObj["data"];
                foreach (JObject imageObj in imageArray)
                {
                    var item = new GiphyItem(imageObj);
                    item.NextPageId = nextPageId;
                    imageItems.Add(item);
                }
                return imageItems;
            }
            catch (Exception) { }

            return imageItems;
        }
    }
}
