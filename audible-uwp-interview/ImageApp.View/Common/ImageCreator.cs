using ImageApp.ViewModel;
using System;
using System.Threading.Tasks;
using Windows.UI.Core;
using Windows.UI.Xaml.Media.Imaging;

namespace ImageApp
{
    public class ImageCreator : ImageCreatorInterface
    {
        public async Task<object> BitmapImageAsync(Uri source)
        {
            BitmapImage bitmap = new BitmapImage();
            await InvokeAsync( () =>
            {
                if (source != null)
                    bitmap.UriSource = source;
            });
            return bitmap;
        }

        public object BitmapImage(Uri source)
        {
            BitmapImage bitmap = new BitmapImage();
            InvokeAsync(() =>
            {
                if (source != null)
                    bitmap.UriSource = source;

            }).Wait();
            return bitmap;
        }

        public Task Run(Action a)
        {
            return Task.Run(a);
        }

        public Task InvokeAsync(Action a)
        {
#if NETFX_CORE
            //System.Diagnostics.Debug.WriteLine(GetType().Name, "Has thread access: " + Dispatcher.HasThreadAccess);
            CoreDispatcher dispatcher = Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher;
            return dispatcher.RunAsync(CoreDispatcherPriority.Normal, () => a()).AsTask();
#elif WINDOWS_PHONE
            Dispatcher dispatcher = Deployment.Current.Dispatcher;
            return ((Dispatcher)dispatcher).InvokeAsync(a);
#else
            Dispatcher dispatcher = Application.Current.Dispatcher;
            return ((Dispatcher)dispatcher).InvokeAsync(a).Task;
#endif
        }
    }
}
