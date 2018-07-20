using System;

#if NETFX_CORE
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Media;
#else
using System.Windows.Data;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
#endif

namespace ImageApp.Common
{
    /// <summary>
    /// Value converter that translates true to <see cref="Visibility.Visible"/> and false to
    /// <see cref="Visibility.Collapsed"/>.
    /// </summary>
    public sealed class StringToImageSourceConverter : IValueConverter
    {
#if NETFX_CORE
        public object Convert(object value, Type targetType, object parameter, string language)
#else
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo language)
#endif
        {
            try
            {
                if (value is BitmapImage)
                {
                    return (ImageSource)value;
                }
                else if (value is string || value is String)
                {
                    //this should never happen
                    return (string)value;
                }
                else if (value is ImageSource)
                {
                    // this should never happen
                    return (ImageSource)value;
                }
            }
            catch (Exception) { }
            // This is probably null
            return value;
        }

#if NETFX_CORE
        public object ConvertBack(object value, Type targetType, object parameter, string language)
#else
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo language)
#endif
        {
            try
            {
                if (value is BitmapImage)
                {
                    return (ImageSource)value;
                }
                else if (value is string || value is String)
                {
                    //this should never happen
                    return (string)value;
                }
                else if (value is ImageSource)
                {
                    // this should never happen
                    return (ImageSource)value;
                }
            }
            catch (Exception) { }
            // This is probably null
            return value;
        }
    }
}