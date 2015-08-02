from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.categories, name='categories'),
    url(r'^category=(?P<category_id>[0-9]+)/$', views.category),
    url(r'^workout=(?P<workout_id>[0-9]+)/$', views.workout),
    url(r'^dump_user_data=(?P<user_id>[0-9]+)/$', views.dump_user_data),
    url(r'^user=(?P<user_id>[0-9]+)/$', views.user_info),
    url(r'^total_calo_week=(?P<user_id>[0-9]+)/$', views.total_calo_week),
]