using System;
using System.Linq;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace ImageApp.View
{
    public class VariableGridView : GridView
    {
        protected override void PrepareContainerForItemOverride(DependencyObject element, object item)
        {
            dynamic model = item;
            try
            {
                element.SetValue(VariableSizedWrapGrid.ColumnSpanProperty, model.ColSpan);
                element.SetValue(VariableSizedWrapGrid.RowSpanProperty, model.RowSpan);
            }
            catch
            {
                element.SetValue(VariableSizedWrapGrid.ColumnSpanProperty, 1);
                element.SetValue(VariableSizedWrapGrid.RowSpanProperty, 1);
            }
            finally
            {
                element.SetValue(VerticalContentAlignmentProperty, VerticalAlignment.Stretch);
                element.SetValue(HorizontalContentAlignmentProperty, HorizontalAlignment.Stretch);
                base.PrepareContainerForItemOverride(element, item);
            }
        }

        // refresh the variablesizedwrapgrid layout
        public void Update()
        {
            if (!(this.ItemsPanelRoot is VariableSizedWrapGrid))
                throw new ArgumentException("ItemsPanel is not VariableSizedWrapGrid");

            foreach (var container in this.ItemsPanelRoot.Children.Cast<GridViewItem>())
            {
                dynamic data = container.Content;
                VariableSizedWrapGrid.SetRowSpan(container, data.RowSpan);
                VariableSizedWrapGrid.SetColumnSpan(container, data.ColSpan);
            }

            this.ItemsPanelRoot.InvalidateMeasure();
        }
    }
}
