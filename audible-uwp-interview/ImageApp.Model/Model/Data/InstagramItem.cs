using Newtonsoft.Json.Linq;
using System;

namespace ImageApp.Model
{
    class InstagramItem : ImageItem
    {
        public InstagramItem(JObject jsonObj)
        {
            try
            {
                Id = (string)jsonObj["id"];
                Url = (string)jsonObj["display_src"];
                Title = (string)jsonObj["caption"];
            }
            catch (Exception) { }
        }
    }
}