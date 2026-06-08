-- ============================================
-- 科研论文文献元数据管理系统 - 测试数据
-- ============================================

USE research_db;

-- ============================================
-- 机构数据
-- ============================================
INSERT INTO institution (name, name_en, country, city, type) VALUES
('清华大学', 'Tsinghua University', 'China', 'Beijing', 'university'),
('北京大学', 'Peking University', 'China', 'Beijing', 'university'),
('浙江大学', 'Zhejiang University', 'China', 'Hangzhou', 'university'),
('上海交通大学', 'Shanghai Jiao Tong University', 'China', 'Shanghai', 'university'),
('中国科学院', 'Chinese Academy of Sciences', 'China', 'Beijing', 'institute'),
('复旦大学', 'Fudan University', 'China', 'Shanghai', 'university'),
('南京大学', 'Nanjing University', 'China', 'Nanjing', 'university'),
('中国科学技术大学', 'University of Science and Technology of China', 'China', 'Hefei', 'university'),
('斯坦福大学', 'Stanford University', 'USA', 'Stanford', 'university'),
('麻省理工学院', 'Massachusetts Institute of Technology', 'USA', 'Cambridge', 'university'),
('哈佛大学', 'Harvard University', 'USA', 'Cambridge', 'university'),
('牛津大学', 'University of Oxford', 'UK', 'Oxford', 'university'),
('剑桥大学', 'University of Cambridge', 'UK', 'Cambridge', 'university'),
('谷歌', 'Google', 'USA', 'Mountain View', 'company'),
('微软研究院', 'Microsoft Research', 'USA', 'Redmond', 'company');

-- ============================================
-- 作者数据
-- ============================================
INSERT INTO author (name, name_en, orcid, h_index, total_citations, total_publications, affiliation_id) VALUES
('张伟', 'Wei Zhang', '0000-0001-2345-6789', 25, 3500, 80, 1),
('李明', 'Ming Li', '0000-0002-3456-7890', 32, 5200, 120, 2),
('王芳', 'Fang Wang', '0000-0003-4567-8901', 18, 2800, 65, 3),
('刘洋', 'Yang Liu', '0000-0004-5678-9012', 22, 3100, 75, 4),
('陈静', 'Jing Chen', '0000-0005-6789-0123', 28, 4200, 95, 1),
('赵强', 'Qiang Zhao', '0000-0006-7890-1234', 15, 1900, 55, 5),
('孙磊', 'Lei Sun', '0000-0007-8901-2345', 20, 2600, 70, 6),
('周琳', 'Lin Zhou', '0000-0008-9012-3456', 16, 2100, 58, 7),
('吴峰', 'Feng Wu', '0000-0009-0123-4567', 24, 3800, 88, 8),
('郑华', 'Hua Zheng', '0000-0010-1234-5678', 19, 2400, 62, 9),
('James Smith', 'James Smith', '0000-0011-2345-6789', 45, 12000, 200, 9),
('Maria Garcia', 'Maria Garcia', '0000-0012-3456-7890', 38, 9800, 180, 10),
('John Doe', 'John Doe', '0000-0013-4567-8901', 42, 11500, 195, 11),
('Jane Wilson', 'Jane Wilson', '0000-0014-5678-9012', 36, 8900, 165, 12),
('Robert Brown', 'Robert Brown', '0000-0015-6789-0123', 40, 10200, 188, 13);

-- ============================================
-- 期刊数据
-- ============================================
INSERT INTO journal (name, name_abbr, type, issn, publisher, impact_factor) VALUES
('Nature', 'Nature', 'journal', '0028-0836', 'Springer Nature', 64.800),
('Science', 'Science', 'journal', '0036-8075', 'AAAS', 56.900),
('Cell', 'Cell', 'journal', '0092-8674', 'Cell Press', 66.850),
('The Lancet', 'Lancet', 'journal', '0140-6736', 'Elsevier', 168.900),
('Journal of the ACM', 'JACM', 'journal', '0004-5411', 'ACM', 5.600),
('IEEE Transactions on Pattern Analysis and Machine Intelligence', 'TPAMI', 'journal', '0162-8828', 'IEEE', 24.314),
('Nature Machine Intelligence', 'Nat Mach Intell', 'journal', '2522-5839', 'Springer Nature', 25.896),
('Proceedings of the National Academy of Sciences', 'PNAS', 'journal', '0027-8424', 'NAS', 12.779),
('Artificial Intelligence', 'Artif Intell', 'journal', '0004-3702', 'Elsevier', 14.000),
('NeurIPS Conference Proceedings', 'NeurIPS', 'conference', NULL, 'NeurIPS Foundation', NULL),
('ICML Conference Proceedings', 'ICML', 'conference', NULL, 'ICML Society', NULL),
('CVPR Conference Proceedings', 'CVPR', 'conference', NULL, 'IEEE/CVF', NULL);

-- ============================================
-- 论文数据
-- ============================================
INSERT INTO paper (title, title_en, abstract, keywords, doi, journal_id, volume, issue, pages, publication_date, publication_year, language, document_type, total_citations, total_references) VALUES
-- 高被引经典论文
('深度学习', 'Deep Learning', '深度学习是机器学习的一个分支，它基于多层神经网络进行表征学习。本文综述了深度学习的基本概念、方法和应用。', '深度学习,神经网络,机器学习,表征学习', '10.1038/nature14539', 1, '521', '7553', '436-444', '2015-05-28', 2015, 'en', 'review', 8500, 150),
('图像识别深度残差学习', 'Deep Residual Learning for Image Recognition', '本文提出了残差学习框架，使得训练非常深的神经网络成为可能。在ImageNet数据集上取得了显著的性能提升。', '深度学习,计算机视觉,图像识别,残差网络', '10.1109/CVPR.2016.90', 12, NULL, NULL, '770-778', '2016-06-27', 2016, 'en', 'article', 12500, 85),
('注意力就是你所需要的', 'Attention Is All You Need', '本文提出了Transformer架构，完全基于注意力机制，摒弃了循环和卷积结构。该架构在机器翻译任务上取得了最先进的结果。', '自然语言处理,Transformer,注意力机制,机器翻译', '10.48550/arXiv.1706.03762', 10, '30', NULL, '5998-6008', '2017-12-04', 2017, 'en', 'article', 18500, 42),
('BERT：语言理解的双向预训练', 'BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding', '本文提出了BERT模型，通过掩码语言模型和下一句预测两个预训练任务，学习深度双向语言表征。', '自然语言处理,预训练模型,BERT,Transformer', '10.18653/v1/N19-1423', 11, '2019', NULL, '4171-4186', '2019-06-02', 2019, 'en', 'article', 15600, 68),
('生成对抗网络', 'Generative Adversarial Networks', '本文提出了生成对抗网络（GAN），通过对抗过程估计生成模型。该框架同时训练两个模型：生成模型和判别模型。', '深度学习,生成模型,对抗学习,GAN', '10.48550/arXiv.1406.2661', 10, '27', NULL, '2672-2680', '2014-12-08', 2014, 'en', 'article', 14200, 55),
('梯度下降学习长时依赖困难', 'Learning Long-Term Dependencies with Gradient Descent is Difficult', '本文分析了为什么使用梯度下降训练循环神经网络时难以学习长时依赖关系。讨论了梯度消失和梯度爆炸问题。', '循环神经网络,梯度消失,长时依赖,RNN', '10.1109/72.279181', 6, '5', '2', '157-166', '1994-03-01', 1994, 'en', 'article', 9800, 120),
('长短期记忆网络', 'Long Short-Term Memory', '本文提出了长短期记忆（LSTM）网络，一种特殊的循环神经网络架构，能够学习长时依赖关系。', '循环神经网络,LSTM,长时依赖,深度学习', '10.1162/neco.1997.9.8.1735', 5, '9', '8', '1735-1780', '1997-11-15', 1997, 'en', 'article', 11200, 95),
('ImageNet大规模视觉识别挑战', 'ImageNet Large Scale Visual Recognition Challenge', '本文介绍了ImageNet大规模视觉识别挑战（ILSVRC），包含超过1400万张标注图像，涵盖2万多个类别。', '计算机视觉,图像分类,ImageNet,数据集', '10.1007/s11263-015-0816-y', 7, '115', '3', '211-252', '2015-12-01', 2015, 'en', 'article', 7800, 180),
('词向量：词语表征的高效估计', 'Efficient Estimation of Word Representations in Vector Space', '本文提出了两种新颖的模型架构：连续词袋模型（CBOW）和跳字模型（Skip-gram），用于学习高质量的词向量。', '自然语言处理,词嵌入,Word2Vec,词向量', '10.48550/arXiv.1301.3781', 10, '2013', NULL, NULL, '2013-01-16', 2013, 'en', 'article', 25000, 45),
('大规模核机器学习', 'Large Scale Kernel Machines', '本文探讨了大规模核机器学习方法，包括支持向量机和核主成分分析等。', '机器学习,核方法,支持向量机,大规模学习', '10.7551/mitpress/5526.001.0001', 3, NULL, NULL, '1-256', '2007-01-01', 2007, 'en', 'book', 6500, 200),
-- 中文论文
('中国人工智能发展现状与展望', 'Current Status and Prospect of Artificial Intelligence Development in China', '本文综述了中国人工智能领域的发展现状，包括基础研究、技术应用和产业发展等方面，并对未来发展趋势进行了展望。', '人工智能,机器学习,发展现状,中国', '10.1360/SSI-2020-0326', 8, '51', '2', '195-218', '2021-02-20', 2021, 'zh', 'article', 320, 45),
('深度学习在医学影像分析中的应用', 'Application of Deep Learning in Medical Image Analysis', '本文系统综述了深度学习在医学影像分析中的应用，包括图像分割、病变检测、计算机辅助诊断等。', '深度学习,医学影像,计算机辅助诊断,图像分割', '10.16438/j.0513-4870.2019-0823', 7, '45', '6', '783-792', '2020-06-15', 2020, 'zh', 'review', 580, 62),
('自然语言处理技术前沿与应用', 'Frontiers and Applications of Natural Language Processing Technology', '本文介绍了自然语言处理技术的最新进展，包括预训练语言模型、机器翻译、情感分析等方向。', '自然语言处理,预训练模型,机器翻译,情感分析', '10.11897/SP.J.1016.2022.00001', 5, '45', '1', '1-25', '2022-01-15', 2022, 'zh', 'review', 290, 38),
('图神经网络研究进展', 'Research Progress of Graph Neural Networks', '图神经网络是处理图结构数据的深度学习方法，本文综述了图神经网络的发展历程、主要方法和应用场景。', '图神经网络,深度学习,图表示学习,节点分类', '10.7544/issn1000-1239.2021.20210267', 9, '58', '5', '928-948', '2021-05-15', 2021, 'zh', 'review', 650, 72),
('联邦学习研究综述', 'A Survey of Federated Learning Research', '联邦学习是一种新兴的机器学习范式，能够在保护数据隐私的前提下进行模型训练。本文系统综述了联邦学习的发展现状和关键技术。', '联邦学习,隐私保护,机器学习,分布式训练', '10.13328/j.cnki.jos.006014', 5, '32', '7', '183-204', '2021-07-15', 2021, 'zh', 'review', 480, 55),
-- 最新研究论文
('GPT-4技术报告', 'GPT-4 Technical Report', '本报告介绍了GPT-4，一个大型多模态模型，能够处理图像和文本输入，在各种专业和学术基准测试中表现出类人水平的性能。', '大语言模型,多模态,GPT-4,人工智能', '10.48550/arXiv.2303.08774', 10, '2023', NULL, NULL, '2023-03-15', 2023, 'en', 'article', 5200, 28),
('扩散模型综述：方法与应用', 'A Comprehensive Survey of Diffusion Models: Methods and Applications', '扩散模型是一类生成模型，通过逐渐从数据中添加噪声，然后学习逆转这个过程来生成数据。本文全面综述了扩散模型的方法和应用。', '扩散模型,生成模型,图像生成,深度学习', '10.48550/arXiv.2209.00796', 10, '2022', NULL, NULL, '2022-09-01', 2022, 'en', 'review', 3800, 156),
('大语言模型综述', 'Large Language Models: A Survey', '大语言模型在自然语言处理领域取得了革命性的进展。本文系统综述了大语言模型的发展历程、关键技术和应用前景。', '大语言模型,自然语言处理,预训练,Transformer', '10.48550/arXiv.2303.18223', 10, '2023', NULL, NULL, '2023-03-31', 2023, 'en', 'review', 4500, 120),
('视觉Transformer综述', 'A Survey of Vision Transformers', '视觉Transformer（ViT）将Transformer架构应用于计算机视觉任务，取得了显著的性能提升。本文综述了视觉Transformer的发展和应用。', '计算机视觉,Transformer,视觉Transformer,图像分类', '10.1007/s11263-022-01684-y', 7, '130', '11', '1810-1846', '2022-11-01', 2022, 'en', 'review', 6200, 135),
('强化学习在机器人控制中的应用', 'Reinforcement Learning for Robot Control: A Survey', '强化学习为机器人控制提供了一种新的范式，使机器人能够通过与环境交互学习最优策略。本文综述了强化学习在机器人控制中的应用进展。', '强化学习,机器人控制,深度学习,策略优化', '10.1016/j.artint.2021.103500', 9, '302', NULL, '103500', '2022-01-01', 2022, 'en', 'review', 2100, 98);

-- ============================================
-- 论文-作者关联数据
-- ============================================
INSERT INTO paper_author (paper_id, author_id, author_order, is_corresponding, affiliation_id) VALUES
(1, 11, 1, 1, 9),
(1, 12, 2, 0, 10),
(1, 13, 3, 0, 11),
(2, 5, 1, 0, 1),
(2, 1, 2, 1, 1),
(2, 11, 3, 0, 9),
(3, 11, 1, 0, 14),
(3, 12, 2, 0, 10),
(3, 13, 3, 1, 11),
(4, 2, 1, 1, 2),
(4, 6, 2, 0, 5),
(4, 14, 3, 0, 12),
(5, 13, 1, 1, 11),
(5, 15, 2, 0, 13),
(6, 5, 1, 1, 1),
(6, 3, 2, 0, 3),
(7, 5, 1, 1, 1),
(7, 1, 2, 0, 1),
(8, 11, 1, 0, 9),
(8, 12, 2, 1, 10),
(9, 13, 1, 1, 14),
(9, 14, 2, 0, 12),
(10, 11, 1, 0, 9),
(10, 15, 2, 1, 13),
(11, 1, 1, 1, 1),
(11, 2, 2, 0, 2),
(11, 3, 3, 0, 3),
(12, 3, 1, 0, 3),
(12, 4, 2, 1, 4),
(13, 2, 1, 1, 2),
(13, 7, 2, 0, 6),
(14, 4, 1, 0, 4),
(14, 8, 2, 1, 7),
(15, 5, 1, 0, 1),
(15, 6, 2, 1, 5),
(16, 14, 1, 1, 14),
(16, 15, 2, 0, 15),
(17, 9, 1, 0, 8),
(17, 10, 2, 1, 9),
(18, 10, 1, 1, 9),
(18, 1, 2, 0, 1),
(19, 7, 1, 0, 6),
(19, 8, 2, 1, 7),
(20, 6, 1, 1, 5),
(20, 9, 2, 0, 8);

-- ============================================
-- 论文-机构关联数据
-- ============================================
INSERT INTO paper_institution (paper_id, institution_id, affiliation_order) VALUES
(1, 9, 1), (1, 10, 2), (1, 11, 3),
(2, 1, 1), (2, 9, 2),
(3, 14, 1), (3, 10, 2), (3, 11, 3),
(4, 2, 1), (4, 5, 2), (4, 12, 3),
(5, 11, 1), (5, 13, 2),
(6, 1, 1), (6, 3, 2),
(7, 1, 1),
(8, 9, 1), (8, 10, 2),
(9, 14, 1), (9, 12, 2),
(10, 9, 1), (10, 13, 2),
(11, 1, 1), (11, 2, 2), (11, 3, 3),
(12, 3, 1), (12, 4, 2),
(13, 2, 1), (13, 6, 2),
(14, 4, 1), (14, 7, 2),
(15, 1, 1), (15, 5, 2),
(16, 14, 1), (16, 15, 2),
(17, 8, 1), (17, 9, 2),
(18, 9, 1), (18, 1, 2),
(19, 6, 1), (19, 7, 2),
(20, 5, 1), (20, 8, 2);

-- ============================================
-- 引用关系数据
-- ============================================
INSERT INTO citation (citing_paper_id, cited_paper_id) VALUES
-- 论文1引用
(1, 6), (1, 7), (1, 10),
-- 论文2引用
(2, 1), (2, 8), (2, 5),
-- 论文3引用
(3, 1), (3, 7), (3, 9),
-- 论文4引用
(4, 3), (4, 9), (4, 7),
-- 论文5引用
(5, 1), (5, 10),
-- 论文6被引（经典）
-- 论文7被引（经典）
-- 论文8引用
(8, 2),
-- 论文9被引（经典）
-- 论文10被引（经典）
-- 论文11引用
(11, 1), (11, 4), (11, 18),
-- 论文12引用
(12, 1), (12, 2), (12, 17),
-- 论文13引用
(13, 3), (13, 4), (13, 9), (13, 18),
-- 论文14引用
(14, 1), (14, 3), (14, 19),
-- 论文15引用
(15, 1), (15, 18),
-- 论文16引用
(16, 3), (16, 4), (16, 18),
-- 论文17引用
(17, 1), (17, 5), (17, 2),
-- 论文18引用
(18, 3), (18, 4), (18, 9),
-- 论文19引用
(19, 2), (19, 3), (19, 8), (19, 17),
-- 论文20引用
(20, 1), (20, 6), (20, 7);

-- ============================================
-- 作者-机构关联历史数据
-- ============================================
INSERT INTO author_institution (author_id, institution_id, start_year, end_year, is_current, position) VALUES
(1, 1, 2010, NULL, 1, 'Professor'),
(2, 2, 2012, NULL, 1, 'Professor'),
(3, 3, 2015, NULL, 1, 'Associate Professor'),
(4, 4, 2014, NULL, 1, 'Professor'),
(5, 1, 2008, NULL, 1, 'Professor'),
(6, 5, 2011, NULL, 1, 'Researcher'),
(7, 6, 2013, NULL, 1, 'Associate Professor'),
(8, 7, 2016, NULL, 1, 'Professor'),
(9, 8, 2012, NULL, 1, 'Professor'),
(10, 9, 2018, NULL, 1, 'Postdoc'),
(11, 9, 2005, NULL, 1, 'Professor'),
(12, 10, 2008, NULL, 1, 'Professor'),
(13, 11, 2006, NULL, 1, 'Professor'),
(14, 12, 2010, NULL, 1, 'Professor'),
(15, 13, 2007, NULL, 1, 'Professor');
