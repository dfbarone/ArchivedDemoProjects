using ImageApp.Common;
using ImageApp.Model;
using System;
using System.IO;
using System.Threading.Tasks;

namespace ImageApp.ViewModel
{
    public class ImageDataItem : BindableBase
    {
        public int Index { get; set; }
        public object ColSpan { get; set; }
        public object RowSpan { get; set; }
        public string Title { get; set; }
        public ImageService ImageType { get; set; }

        private ImageCreatorInterface _bitmapImageCreator = null;
 
        public ImageDataItem(int index, string title, string url, ImageService serviceType, ImageCreatorInterface bitmapImageCreator)
        {
            Index = index;
            Title = title;
            ImagePath = url;
            ImageType = serviceType;

            ColSpan = CalcColSpan(index);
            RowSpan = CalcRowSpan(index);

            _bitmapImageCreator = bitmapImageCreator;
        }

        public async Task<bool> Init()
        {
            if (ImageType != ImageService.Giphy)
            {
                _image = await _bitmapImageCreator.BitmapImageAsync(new Uri(_imagePath));
            }

            return true;
        }

        private bool _play = false;
        public bool Play
        {
            get { return _play; }
            set { this.SetProperty(ref this._play, value, "Play"); }
        }

        protected object _image = null;
        public string _imagePath = null;

        public string ImagePath
        {
            get { return _imagePath; }
            set
            {
                this._imagePath = null;
                this.SetProperty(ref this._imagePath, value, "ImagePath");
            }
        }

        public object Image
        {
            get
            {
                //if (this._image == null && this._imagePath != null)
                //{
                //    Uri uri = new Uri(_imagePath, UriKind.RelativeOrAbsolute);
                //    _image = _bitmapImageCreator.BitmapImage(uri);
                //}
                return this._image;
            }
            set
            {
                this._imagePath = null;
                this.SetProperty(ref this._image, value, "Image");
            }
        }

        // Pull new image from web
        public void SetImage(string path)
        {
            this._image = null;
            this._imagePath = path;
            this.OnPropertyChanged("Image");
        }

        public static int CalcColSpan(int i)
        {
            return (i % 3 == 0) ? 2 : 1;
        }

        public static int CalcRowSpan(int i)
        {
            return (i % 3 == 0) ? 2 : 1;
        }
    }
}
