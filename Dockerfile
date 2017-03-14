FROM centos

RUN rpm --import http://repos.azulsystems.com/RPM-GPG-KEY-azulsystems

RUN curl -o java.rpm http://cdn.azul.com/zulu/bin/zulu8.20.0.5-jdk8.0.121-linux.x86_64.rpm

RUN yum install -y java.rpm epel-release centos-release-openshift-origin && rm java.rpm

RUN yum install -y git ansible openssh python-cryptography pyOpenSSL libselinux-python && \
    yum clean all

WORKDIR /root

RUN git clone https://github.com/openshift/openshift-ansible.git
RUN cd openshift-ansible && git checkout release-1.4 && cd ..

ENV OPENSHIFT_ANSIBLE=/root/openshift-ansible

ADD out /root/

WORKDIR /root/data

VOLUME ['/root/data']

ENTRYPOINT [ "/usr/bin/java", "-classpath", "../openshifter.jar", "eu.mjelen.openshifter.cli.Main" ]
