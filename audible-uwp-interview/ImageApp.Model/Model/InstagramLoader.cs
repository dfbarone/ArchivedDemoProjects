using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    class InstagramLoader : Loader<ImageItem>
    {
        public InstagramLoader(string search, string pagination)
        {
            string url = string.Format("https://www.instagram.com/explore/tags/{0}/", Uri.EscapeDataString(search));

            if (pagination.Length > 0)
                url += "?max_id=" + pagination;

            Url = url;
        }

        // Not ideal, but screen scraping is the only way to get these photos at the moment
        override
        public async Task<List<ImageItem>> Load()
        {
            List<ImageItem> imageItems = new List<ImageItem>();

            try
            {
                string content = await GetAsync(Url);
                content = content.FindJSON("_sharedData =");
                JObject jsonObj = JObject.Parse(content);

                // drill down to find image array
                jsonObj = (JObject)jsonObj["entry_data"];
                JArray tagPageArray = (JArray)jsonObj["TagPage"];
                jsonObj = (JObject)tagPageArray.First();
                jsonObj = (JObject)jsonObj["tag"];
                jsonObj = (JObject)jsonObj["media"];
                JObject pageInfoJsonObj = (JObject)jsonObj["page_info"];

                // get next page id
                string nextPageId = (string)pageInfoJsonObj["end_cursor"];

                // parse images
                JArray imageArray = (JArray)jsonObj["nodes"];
                foreach (JObject imageObj in imageArray)
                {
                    var item = new InstagramItem(imageObj);
                    item.NextPageId = nextPageId;
                    imageItems.Add(item);
                }
                return imageItems;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("Exception: " + e.Message);
            }

            return imageItems;
        }
    }
}
