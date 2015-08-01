from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.categories, name='categories'),
    url(r'^category=(?P<category_id>[0-9]+)/$', views.category, name='category'),
    url(r'^workout=(?P<workout_id>[0-9]+)/$', views.workout, name='workout'),
    url(r'^add_workout/(?P<category_id>[0-9]+/)&name=<name>&estimated_calories=<estimated_calories>'
        r'&description=<description>&video_url=<video_url>&<tags>$', views.add_workout, name='add_workout')
]