from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.categories, name='categories'),
    url(r'^category=(?P<category_id>[0-9]+)/$', views.category, name='category'),
    url(r'^workout=(?P<workout_id>[0-9]+)/$', views.workout, name='workout'),
    url(r'^dump_data_path=(?P<user_id>[0-9]+)/$', views.dump_data_path, name='dump_data_path'),
    url(r'^user=(?P<user_id>[0-9]+)/$', views.return_user_info, name='return_user_info'),

]