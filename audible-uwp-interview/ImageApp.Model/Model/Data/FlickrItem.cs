using Newtonsoft.Json.Linq;
using System;

namespace ImageApp.Model
{
    class FlickrItem : ImageItem
    {
        public FlickrItem(JObject jsonObj)
        {
            try
            {
                Id = (string)jsonObj["id"];
                Title = (string)jsonObj["title"];

                Owner = (string)jsonObj["owner"];
                string secret = (string)jsonObj["secret"];
                string server = (string)jsonObj["server"];
                int farm = (int)jsonObj["farm"];
                
                Url = string.Format("http://farm{0}.staticflickr.com/{1}/{2}_{3}.jpg", farm, server, Id, secret);
            }
            catch (Exception) { }
        }
    }
}