from django.db import models
from jsonfield import JSONField
import pafy


class Category(models.Model):
    name = models.TextField()
    description = models.TextField()
    exerciseNumber = models.IntegerField(default=0)

    def __str__(self):
        return self.name

    def as_json(self):
        return dict(name=self.name, description=self.description)


class Workout(models.Model):
    category = models.ForeignKey(Category)
    name = models.TextField()
    estimated_calories = models.IntegerField()
    description = models.TextField()
    video_url = models.URLField()
    tags = models.TextField(default=None)

    def __str__(self):
        return self.name

    def as_json(self):
        return dict(name=self.name, estimated_calories=self.estimated_calories,
                    description=self.description, video_url=self.video_url,
                    video_stream=pafy.new(self.video_url).getbest().url, tags=self.tags)


class User(models.Model):
    email = models.EmailField()
    history = JSONField()

    def as_json(self):
        return dict(email=self.email, history=self.history)




