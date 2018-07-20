using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    class GiphyItem : ImageItem
    {
        private string Slug { get; set; }
        
        public GiphyItem(JObject jsonObj)
        {
            try
            {
                Id = (string)jsonObj["id"];
                Slug = (string)jsonObj["slug"];
                Owner = (string)jsonObj["username"];
                JObject imagesObj = (JObject)jsonObj["images"];
                JToken fixedHeightImageObj = (JToken)imagesObj["fixed_height"];
                Url = (String)fixedHeightImageObj["url"];
            }
            catch (Exception) { }
        }
    }
}
