using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;

namespace ImageApp.Model
{
    abstract class Loader<T>
    {
        protected string Url { get; set; }
        protected string Pagination { get; set; }

        private static HttpClient client = new HttpClient();

        public async Task<string> GetAsync(string url)
        {
            string payload = string.Empty;

            try
            {
                var response = await client.GetAsync(url);
                response.EnsureSuccessStatusCode();
                payload = await response.Content.ReadAsStringAsync();
            }
            catch (Exception) { }

            return payload;
        }

        public abstract Task<List<T>> Load();
    }

    class WebServiceManager
    {
        public Task<List<T>> Load<T>(Loader<T> loader)
        {
            return loader.Load();
        }
    }
}

public static class StringExtentions
{
    public static string TrimOuter(this string content, string trim1, string trim2)
    {
        int first = content.IndexOf(trim1);
        int last = content.LastIndexOf(trim2);
        return content.Substring(first, last - first + 1);
    }

    public static string FindJSON(this string content, string startingIndicator)
    {
        int startingPosition = content.IndexOf(startingIndicator);
        string newContent = content.Substring(startingPosition + startingIndicator.Length);

        int bracketCount = 0;
        string jsonString = string.Empty;
        for (int i = 0; i < newContent.Length; i++)
        {
            string str = newContent[i].ToString();
            if (str == "{")
                bracketCount++;
            else if (str == "}")
                bracketCount--;

            if (bracketCount == 0 && i > 0 && string.IsNullOrEmpty(jsonString))
            {
                jsonString = newContent.Substring(0, i + 1);
                break;
            }
        }
        return jsonString;
    }
}

