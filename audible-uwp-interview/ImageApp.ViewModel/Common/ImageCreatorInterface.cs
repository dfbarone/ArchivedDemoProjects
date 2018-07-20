using System;
using System.Threading.Tasks;

namespace ImageApp.ViewModel
{
    public interface ImageCreatorInterface
    {
        Task<object> BitmapImageAsync(Uri source);
        object BitmapImage(Uri source);

        Task Run(Action a);
        Task InvokeAsync(Action a);

    }
}
